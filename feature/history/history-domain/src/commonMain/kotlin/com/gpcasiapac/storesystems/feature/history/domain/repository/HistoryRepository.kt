package com.gpcasiapac.storesystems.feature.history.domain.repository

import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItemWithCollectMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItemWithMetadata
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for history data access.
 * Implementation delegates to SyncQueueService.
 */
interface HistoryRepository {
    /**
     * Observe all history items with metadata.
     * This is the primary method for observing history.
     */
    fun observeHistoryWithMetadata(): Flow<List<HistoryItemWithMetadata>>
    
    /**
     * Get history for a specific entity ID.
     */
    suspend fun getHistoryByEntityId(entityId: String): List<HistoryItemWithMetadata>
    
    /**
     * Delete a history item (deletes the corresponding sync task).
     */
    suspend fun deleteHistoryItem(id: String): Result<Unit>
    
    /**
     * Retry a failed history item (resets the sync task to pending).
     */
    suspend fun retryHistoryItem(id: String): Result<Unit>
    
    /**
     * Get history by invoice number.
     */
    suspend fun getHistoryByInvoiceNumber(invoiceNumber: String): List<HistoryItemWithMetadata>
    
    /**
     * Get history by customer number.
     */
    suspend fun getHistoryByCustomerNumber(customerNumber: String): List<HistoryItemWithMetadata>
    
    // DEPRECATED METHODS - Keep for backward compatibility during migration
    @Deprecated("Use observeHistoryWithMetadata() instead", ReplaceWith("observeHistoryWithMetadata()"))
    fun observeHistory(): Flow<List<HistoryItem>>
    
    @Deprecated("Use observeHistoryWithMetadata() instead")
    fun observeHistoryWithCollectMetadata(): Flow<List<HistoryItemWithCollectMetadata>>
    
    @Deprecated("Use getHistoryByEntityId() instead")
    suspend fun getHistoryWithCollectMetadataByEntityId(entityId: String): List<HistoryItemWithCollectMetadata>
}
