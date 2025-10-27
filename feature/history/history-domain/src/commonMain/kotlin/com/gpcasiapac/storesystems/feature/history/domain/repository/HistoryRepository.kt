package com.gpcasiapac.storesystems.feature.history.domain.repository

import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItemWithCollectMetadata
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for history data access.
 * Implementation delegates to SyncQueueService.
 */
interface HistoryRepository {
    /**
     * Observe all history items (maps from all sync tasks).
     */
    fun observeHistory(): Flow<List<HistoryItem>>
    
    /**
     * Get history for a specific entity ID.
     */
    suspend fun getHistoryByEntityId(entityId: String): List<HistoryItem>
    
    /**
     * Delete a history item (deletes the corresponding sync task).
     */
    suspend fun deleteHistoryItem(id: String): Result<Unit>
    
    /**
     * Retry a failed history item (resets the sync task to pending).
     */
    suspend fun retryHistoryItem(id: String): Result<Unit>
    
    /**
     * Observe all history items with collect metadata.
     * Only returns tasks that have collect metadata attached.
     */
    fun observeHistoryWithCollectMetadata(): Flow<List<HistoryItemWithCollectMetadata>>
    
    /**
     * Get history with collect metadata for a specific entity ID.
     */
    suspend fun getHistoryWithCollectMetadataByEntityId(entityId: String): List<HistoryItemWithCollectMetadata>
    
    /**
     * Get history by invoice number.
     */
    suspend fun getHistoryByInvoiceNumber(invoiceNumber: String): List<HistoryItemWithCollectMetadata>
    
    /**
     * Get history by customer number.
     */
    suspend fun getHistoryByCustomerNumber(customerNumber: String): List<HistoryItemWithCollectMetadata>
}
