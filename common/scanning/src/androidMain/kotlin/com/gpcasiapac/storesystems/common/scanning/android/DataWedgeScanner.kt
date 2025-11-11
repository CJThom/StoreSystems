package com.gpcasiapac.storesystems.common.scanning.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.common.scanning.ScanResult
import com.gpcasiapac.storesystems.common.scanning.Scanner
import com.gpcasiapac.storesystems.common.scanning.ScannerStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Zebra DataWedge-backed scanner implementation (clean Option B).
 * - Delegates profile creation/update to DataWedgeProfileManager
 * - Uses a small decode data parser
 * - Avoids extra coroutine scope; uses tryEmit()
 */
class DataWedgeScanner(
    private val appContext: Context,
    private val logger: Logger,
    private val profileName: String,
    private val intentAction: String,
    private val autoCreateProfile: Boolean = true,
) : Scanner {

    private val log = logger.withTag("DataWedgeScanner")

    private val _scans = MutableSharedFlow<ScanResult>(extraBufferCapacity = 64)
    override val scans = _scans.asSharedFlow()

    private val _status = MutableStateFlow<ScannerStatus>(ScannerStatus.Idle)
    override val status = _status.asStateFlow()

    private var resultsReceiverRegistered = false

    private val resultsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != intentAction) return
            val data = intent.getStringExtra("com.symbol.datawedge.data_string") ?: return
            val labelType = intent.getStringExtra("com.symbol.datawedge.label_type")
            val source = intent.getStringExtra("com.symbol.datawedge.source")
            log.i { "Scan received: text='${data.take(64)}' symbology=$labelType source=$source" }
            _scans.tryEmit(
                ScanResult(
                    text = data,
                    symbology = labelType,
                    source = source
                )
            )
        }
    }

    override fun start() {
        val installed = isDataWedgeInstalled()
        log.i { "start(): profile='$profileName', action='$intentAction', dataWedgeInstalled=$installed" }
        if (autoCreateProfile) {
            DataWedgeProfileManager(appContext, profileName, intentAction, log).ensureProfile()
        }
        registerReceiverIfNeeded()
        _status.value = ScannerStatus.Enabled
    }

    override fun stop() {
        unregisterReceiverIfNeeded()
        _status.value = ScannerStatus.Disabled
        log.i { "stop()" }
    }

    override fun trigger(on: Boolean) {
        DataWedgeIntents.sendCommand(
            appContext,
            DataWedgeIntents.EXTRA_SOFT_SCAN_TRIGGER,
            if (on) "START_SCANNING" else "STOP_SCANNING"
        )
        log.d { "trigger(on=$on)" }
    }

    override fun release() { stop() }

    private fun registerReceiverIfNeeded() {
        if (!resultsReceiverRegistered) {
            runCatching {
                val filter = IntentFilter().apply {
                    addAction(intentAction)
                    addCategory(Intent.CATEGORY_DEFAULT)
                }
                // Android 13+ requires explicit exported flag for non-system broadcasts
                if (Build.VERSION.SDK_INT >= 33) {
                    appContext.registerReceiver(
                        resultsReceiver,
                        filter,
                        Context.RECEIVER_EXPORTED
                    )
                } else {
                    @Suppress("DEPRECATION")
                    appContext.registerReceiver(resultsReceiver, filter)
                }
                resultsReceiverRegistered = true
                log.i { "Registered scan results receiver for action='$intentAction'" }
            }.onFailure { t -> log.e(t) { "Failed to register scan results receiver" } }
        }
    }

    private fun unregisterReceiverIfNeeded() {
        if (resultsReceiverRegistered) {
            runCatching { appContext.unregisterReceiver(resultsReceiver) }
                .onSuccess { log.i { "Unregistered scan results receiver" } }
                .onFailure { t -> log.e(t) { "Failed to unregister scan results receiver" } }
            resultsReceiverRegistered = false
        }
    }

    private fun isDataWedgeInstalled(): Boolean = runCatching {
        @Suppress("DEPRECATION")
        appContext.packageManager.getPackageInfo("com.symbol.datawedge", 0)
        true
    }.getOrElse { false }
}
