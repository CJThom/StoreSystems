package com.gpcasiapac.storesystems.feature.history.domain.repository

import com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for history data access.
 * Implementation delegates to SyncQueueService.
 */
interface HistoryRepository {
    /**
     * Observe all history items (sealed types).
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
     * Get history by invoice number.
     */
    suspend fun getHistoryByInvoiceNumber(invoiceNumber: String): List<HistoryItem>

    /**
     * Get history by customer number.
     */
    suspend fun getHistoryByCustomerNumber(customerNumber: String): List<HistoryItem>

    /**
     * Type-specific method: Get a Collect history item by its task id.
     */
    suspend fun getCollectItemById(id: String): CollectHistoryItem?
}
