package com.gpcasiapac.storesystems.common.scanning.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.common.scanning.ScanResult
import com.gpcasiapac.storesystems.common.scanning.Scanner
import com.gpcasiapac.storesystems.common.scanning.ScannerStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Zebra DataWedge-backed scanner implementation.
 */
class DataWedgeScanner(
    private val appContext: Context,
    private val logger: Logger,
    private val profileName: String,
    private val intentAction: String,
    private val autoCreateProfile: Boolean = true,
) : Scanner {

    private val log = logger.withTag("DataWedgeScanner")

    private val scope = CoroutineScope(Dispatchers.Default)
    private val _scans = MutableSharedFlow<ScanResult>(extraBufferCapacity = 64)
    override val scans = _scans.asSharedFlow()

    private val _status = MutableStateFlow<ScannerStatus>(ScannerStatus.Idle)
    override val status = _status.asStateFlow()

    private var resultsReceiverRegistered = false
    private var apiResultReceiverRegistered = false

    private val resultsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != intentAction) {
                log.v { "Ignoring intent action='${intent.action}' (expect '$intentAction')" }
                return
            }
            val data = intent.getStringExtra("com.symbol.datawedge.data_string")
            val labelType = intent.getStringExtra("com.symbol.datawedge.label_type")
            val source = intent.getStringExtra("com.symbol.datawedge.source")
            val raw = intent.getByteArrayExtra("com.symbol.datawedge.decode_data")
            if (data != null) {
                log.i { "Scan received: text='${data.take(64)}' symbology=$labelType source=$source bytes=${raw?.size ?: 0}" }
                scope.launch {
                    _scans.emit(
                        ScanResult(
                            text = data,
                            symbology = labelType,
                            rawBytes = raw,
                            source = source
                        )
                    )
                }
            } else {
                log.w { "Scan intent received but data_string was null. Extras=${intent.extras?.keySet()?.joinToString()}" }
            }
        }
    }

    private val apiResultReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Log every DataWedge API result for visibility
            val action = intent.action
            val extras = intent.extras
            val keys = extras?.keySet()?.joinToString()
            log.d { "DW RESULT on action=$action, keys=$keys" }
            if (extras != null) {
                for (k in extras.keySet()) {
                    val v = extras.get(k)
                    log.v { "  $k = $v" }
                }
            }
        }
    }

    override fun start() {
        val installed = isDataWedgeInstalled()
        log.i { "start(): profile='$profileName', action='$intentAction', dataWedgeInstalled=$installed" }
        _status.value = ScannerStatus.Enabled
        registerReceiverIfNeeded()
        if (autoCreateProfile) ensureProfile()
        // Request diagnostics from DataWedge so we can see responses in RESULT_ACTION logs
        sendCommand("com.symbol.datawedge.api.GET_VERSION_INFO", "")
        sendCommand("com.symbol.datawedge.api.GET_ACTIVE_PROFILE", "")
    }

    override fun stop() {
        log.i { "stop()" }
        unregisterReceiverIfNeeded()
        _status.value = ScannerStatus.Disabled
    }

    override fun trigger(on: Boolean) {
        log.d { "trigger(on=$on)" }
        sendCommand(
            "com.symbol.datawedge.api.SOFT_SCAN_TRIGGER",
            if (on) "START_SCANNING" else "STOP_SCANNING"
        )
    }

    override fun release() { stop() }

    private fun registerReceiverIfNeeded() {
        if (!resultsReceiverRegistered) {
            runCatching {
                val filter = IntentFilter().apply {
                    addAction(intentAction)
                    addCategory(Intent.CATEGORY_DEFAULT)
                }
                appContext.registerReceiver(resultsReceiver, filter)
                resultsReceiverRegistered = true
                log.i { "Registered scan results receiver for action='$intentAction'" }
            }.onFailure { t ->
                log.e(t) { "Failed to register scan results receiver" }
            }
        }

        if (!apiResultReceiverRegistered) {
            runCatching {
                val resultFilter = IntentFilter().apply {
                    addAction("com.symbol.datawedge.api.RESULT_ACTION")
                }
                appContext.registerReceiver(apiResultReceiver, resultFilter)
                apiResultReceiverRegistered = true
                log.i { "Registered DataWedge API result receiver" }
            }.onFailure { t ->
                log.e(t) { "Failed to register API result receiver" }
            }
        }
    }

    private fun unregisterReceiverIfNeeded() {
        if (resultsReceiverRegistered) {
            runCatching { appContext.unregisterReceiver(resultsReceiver) }
                .onSuccess { log.i { "Unregistered scan results receiver" } }
                .onFailure { t -> log.e(t) { "Failed to unregister scan results receiver" } }
            resultsReceiverRegistered = false
        }
        if (apiResultReceiverRegistered) {
            runCatching { appContext.unregisterReceiver(apiResultReceiver) }
                .onSuccess { log.i { "Unregistered API result receiver" } }
                .onFailure { t -> log.e(t) { "Failed to unregister API result receiver" } }
            apiResultReceiverRegistered = false
        }
    }

    private fun ensureProfile() {
        log.i { "ensureProfile(): creating/updating DataWedge profile '$profileName' with action '$intentAction'" }
        // 1) Create profile (idempotent)
        sendCommand("com.symbol.datawedge.api.CREATE_PROFILE", profileName)

        // Prepare base app association once
        val appConfig = Bundle().apply {
            putString("PACKAGE_NAME", appContext.packageName)
            putStringArray("ACTIVITY_LIST", arrayOf("*"))
        }

        // 2) Configure BARCODE plugin first
        val barcodeSetConfig = Bundle().apply {
            putString("PROFILE_NAME", profileName)
            putString("PROFILE_ENABLED", "true")
            putString("CONFIG_MODE", "UPDATE")
            putParcelableArray("APP_LIST", arrayOf(appConfig))
            putBundle("PLUGIN_CONFIG", Bundle().apply {
                putString("PLUGIN_NAME", "BARCODE")
                putString("RESET_CONFIG", "true")
                putBundle("PARAM_LIST", Bundle().apply {
                    putString("decoder_code128", "true")
                    putString("decoder_ean13", "true")
                    putString("decoder_ean8", "true")
                    putString("decoder_qr_code", "true")
                    putString("configure_all_scanners", "true")
                    putString("scanner_input_enabled", "true")
                })
            })
        }
        log.d { "SET_CONFIG (BARCODE) for profile '$profileName'" }
        sendCommandBundle("com.symbol.datawedge.api.SET_CONFIG", barcodeSetConfig)

        // 3) Configure INTENT plugin (Broadcast delivery)
        val intentSetConfig = Bundle().apply {
            putString("PROFILE_NAME", profileName)
            putString("PROFILE_ENABLED", "true")
            putString("CONFIG_MODE", "UPDATE")
            putParcelableArray("APP_LIST", arrayOf(appConfig))
            putBundle("PLUGIN_CONFIG", Bundle().apply {
                putString("PLUGIN_NAME", "INTENT")
                putString("RESET_CONFIG", "true")
                putBundle("PARAM_LIST", Bundle().apply {
                    putString("intent_output_enabled", "true")
                    putString("intent_action", intentAction)
                    putString("intent_delivery", "2") // 0 = StartActivity, 1 = StartService, 2 = Broadcast
                    putString("intent_category", Intent.CATEGORY_DEFAULT)
                    putString("intent_use_content_provider", "false")
                })
            })
        }
        log.d { "SET_CONFIG (INTENT) for profile '$profileName' action='$intentAction'" }
        sendCommandBundle("com.symbol.datawedge.api.SET_CONFIG", intentSetConfig)

        // 4) Disable keystroke output to avoid duplicate events
        val keystrokeSetConfig = Bundle().apply {
            putString("PROFILE_NAME", profileName)
            putString("PROFILE_ENABLED", "true")
            putString("CONFIG_MODE", "UPDATE")
            putParcelableArray("APP_LIST", arrayOf(appConfig))
            putBundle("PLUGIN_CONFIG", Bundle().apply {
                putString("PLUGIN_NAME", "KEYSTROKE")
                putString("RESET_CONFIG", "true")
                putBundle("PARAM_LIST", Bundle().apply {
                    putString("keystroke_output_enabled", "false")
                })
            })
        }
        log.d { "SET_CONFIG (KEYSTROKE disable) for profile '$profileName'" }
        sendCommandBundle("com.symbol.datawedge.api.SET_CONFIG", keystrokeSetConfig)

        // 5) Make this profile active now
        log.d { "Switching active profile to '$profileName'" }
        sendCommand("com.symbol.datawedge.api.SWITCH_TO_PROFILE", profileName)
    }

    private fun sendCommand(extraKey: String, extraValue: String) {
        val intent = Intent().apply {
            action = "com.symbol.datawedge.api.ACTION"
            putExtra(extraKey, extraValue)
        }
        log.d { "Sending DW command: key=$extraKey, value=$extraValue" }
        appContext.sendBroadcast(intent)
    }

    private fun sendCommandBundle(extraKey: String, bundle: Bundle) {
        val intent = Intent().apply {
            action = "com.symbol.datawedge.api.ACTION"
            putExtra(extraKey, bundle)
        }
        log.d { "Sending DW bundle command: key=$extraKey, bundleKeys=${bundle.keySet()}" }
        appContext.sendBroadcast(intent)
    }

    private fun isDataWedgeInstalled(): Boolean = runCatching {
        @Suppress("DEPRECATION")
        appContext.packageManager.getPackageInfo("com.symbol.datawedge", 0)
        true
    }.getOrElse { false }
}
