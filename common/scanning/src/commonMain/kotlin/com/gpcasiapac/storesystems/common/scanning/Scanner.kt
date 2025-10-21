package com.gpcasiapac.storesystems.common.scanning

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/** Represents a successful decode */
data class ScanResult(
    val text: String,
    val symbology: String? = null,   // e.g., "LABEL-TYPE-CODE128"
    val timestamp: Long = System.currentTimeMillis(),
    val rawBytes: ByteArray? = null, // optional, e.g., for GS1 parsing
    val source: String? = null       // e.g., "BARCODE", "MSR"
)

sealed class ScannerStatus {
    data object Idle : ScannerStatus()
    data object Scanning : ScannerStatus()
    data object Enabled : ScannerStatus()
    data object Disabled : ScannerStatus()
    data class Error(val message: String) : ScannerStatus()
}

/** Cross-platform facade */
interface Scanner {
    val scans: Flow<ScanResult>
    val status: StateFlow<ScannerStatus>

    fun start() // start receiving
    fun stop()  // stop receiving

    /** Optional: trigger hardware scan button programmatically if supported */
    fun trigger(on: Boolean) {}

    fun release() {}
}

/** Platform-specific implementation is bound in DI. */
interface ScannerFactory { fun create(): Scanner }

/** Simple Fake implementation usable in non-Android targets and tests. */
class FakeScanner : Scanner {
    private val _scans = MutableSharedFlow<ScanResult>(extraBufferCapacity = 16)
    override val scans = _scans.asSharedFlow()

    private val _status = MutableStateFlow<ScannerStatus>(ScannerStatus.Disabled)
    override val status: StateFlow<ScannerStatus> = _status.asStateFlow()

    override fun start() { _status.value = ScannerStatus.Enabled }
    override fun stop() { _status.value = ScannerStatus.Disabled }

    suspend fun feed(text: String) { _scans.emit(ScanResult(text)) }
}
