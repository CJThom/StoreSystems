package com.gpcasiapac.storesystems.common.scanning

sealed class ScannerStatus {
    data object Idle : ScannerStatus()
    data object Scanning : ScannerStatus()
    data object Enabled : ScannerStatus()
    data object Disabled : ScannerStatus()
    data class Error(val message: String) : ScannerStatus()
}