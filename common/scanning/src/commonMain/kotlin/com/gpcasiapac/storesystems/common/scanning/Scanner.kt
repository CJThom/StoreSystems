package com.gpcasiapac.storesystems.common.scanning

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

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

