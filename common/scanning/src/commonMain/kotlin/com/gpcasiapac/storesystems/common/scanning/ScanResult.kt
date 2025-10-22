package com.gpcasiapac.storesystems.common.scanning

/** Represents a successful decode */
data class ScanResult(
    val text: String,
    val symbology: String? = null,   // e.g., "LABEL-TYPE-CODE128"
    val timestamp: Long = System.currentTimeMillis(),
    val source: String? = null       // e.g., "BARCODE", "MSR"
)