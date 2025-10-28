package com.gpcasiapac.storesystems.feature.history.domain.model

import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

/**
 * Unified domain model for history items with typed metadata.
 * This model combines base history information with optional metadata.
 * 
 * Uses sealed interface for metadata to enable type-safe handling in UI layer.
 */
@OptIn(ExperimentalTime::class)
data class HistoryItemWithMetadata(
    // Base history fields
    val id: String,
    val type: HistoryType,
    val entityId: String,
    val status: HistoryStatus,
    val timestamp: Instant,
    val attempts: Int,
    val lastError: String?,
    val priority: Int,
    
    // Typed metadata
    val metadata: HistoryMetadata
) {
    /**
     * Convert to base HistoryItem (without metadata).
     */
    fun toHistoryItem(): HistoryItem {
        return HistoryItem(
            id = id,
            type = type,
            entityId = entityId,
            status = status,
            timestamp = timestamp,
            attempts = attempts,
            lastError = lastError,
            priority = priority
        )
    }
    
    /**
     * Check if this item has collect metadata.
     */
    fun hasCollectMetadata(): Boolean = metadata is HistoryMetadata.CollectMetadata
    
    /**
     * Safely get collect metadata if available.
     */
    fun getCollectMetadata(): HistoryMetadata.CollectMetadata? {
        return metadata as? HistoryMetadata.CollectMetadata
    }
}
