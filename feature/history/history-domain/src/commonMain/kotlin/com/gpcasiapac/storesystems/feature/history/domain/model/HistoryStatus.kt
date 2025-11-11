package com.gpcasiapac.storesystems.feature.history.domain.model

/**
 * Status of history item.
 * Maps from TaskStatus.
 */
enum class HistoryStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    RETRYING,
    REQUIRES_ACTION;
    
    companion object {
        fun fromTaskStatusName(name: String): HistoryStatus {
            return when (name) {
                "PENDING" -> PENDING
                "IN_PROGRESS" -> IN_PROGRESS
                "COMPLETED" -> COMPLETED
                "FAILED" -> FAILED
                "RETRYING" -> RETRYING
                "REQUIRES_ACTION" -> REQUIRES_ACTION
                else -> PENDING
            }
        }
    }
}
