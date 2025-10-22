package com.gpcasiapac.storesystems.common.scanning

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

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