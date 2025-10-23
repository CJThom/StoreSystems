package com.gpcasiapac.storesystems.feature.history.domain.model

/**
 * Type of history item.
 * Maps from TaskType.
 */
enum class HistoryType {
    ORDER_SUBMISSION,
    UNKNOWN;
    
    companion object {
        fun fromTaskTypeName(name: String): HistoryType {
            return when (name) {
                "COLLECT_SUBMIT_ORDER" -> ORDER_SUBMISSION
                else -> UNKNOWN
            }
        }
    }
}
