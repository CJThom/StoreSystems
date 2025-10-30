package com.gpcasiapac.storesystems.feature.history.data.repository

import com.gpcasiapac.storesystems.core.sync_queue.api.SyncQueueService
import com.gpcasiapac.storesystems.feature.history.data.mapper.toHistoryItems
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of HistoryRepository that delegates to SyncQueueService.
 * Maps SyncTaskWithCollectMetadata to sealed HistoryItem for the domain layer.
 */
class HistoryRepositoryImpl(
    private val syncQueueService: SyncQueueService
) : HistoryRepository {

    override fun observeHistory(): Flow<List<HistoryItem>> {
        return syncQueueService.observeAllTasksWithCollectMetadata()
            .map { tasks -> tasks.toHistoryItems() }
    }

    override suspend fun getHistoryByEntityId(entityId: String): List<HistoryItem> {
        return syncQueueService.getTasksWithCollectMetadataByEntityId(entityId)
            .toHistoryItems()
    }

    override suspend fun deleteHistoryItem(id: String): Result<Unit> {
        return syncQueueService.deleteTask(id)
    }

    override suspend fun retryHistoryItem(id: String): Result<Unit> {
        // Retry uses the existing retryFailedTasks API (no single-task retry available yet)
        return syncQueueService.retryFailedTasks().map { Unit }
    }

    override suspend fun getHistoryByInvoiceNumber(invoiceNumber: String): List<HistoryItem> {
        return syncQueueService.getTasksByInvoiceNumber(invoiceNumber)
            .toHistoryItems()
    }

    override suspend fun getHistoryByCustomerNumber(customerNumber: String): List<HistoryItem> {
        return syncQueueService.getTasksByCustomerNumber(customerNumber)
            .toHistoryItems()
    }
}
