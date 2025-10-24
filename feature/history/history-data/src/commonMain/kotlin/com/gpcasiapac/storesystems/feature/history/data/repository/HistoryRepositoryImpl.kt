package com.gpcasiapac.storesystems.feature.history.data.repository

import com.gpcasiapac.storesystems.core.sync_queue.api.SyncQueueService
import com.gpcasiapac.storesystems.feature.history.data.mapper.toHistoryItem
import com.gpcasiapac.storesystems.feature.history.data.mapper.toHistoryItems
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of HistoryRepository that delegates to SyncQueueService.
 * Maps SyncTask to HistoryItem for domain layer.
 */
class HistoryRepositoryImpl(
    private val syncQueueService: SyncQueueService
) : HistoryRepository {
    
    override fun observeHistory(): Flow<List<HistoryItem>> {
        return syncQueueService.observeAllTasks()
            .map { syncTasks -> syncTasks.toHistoryItems() }
    }
    
    override suspend fun getHistoryByEntityId(entityId: String): List<HistoryItem> {
        return syncQueueService.getTasksByEntityId(entityId).toHistoryItems()
    }
    
    override suspend fun deleteHistoryItem(id: String): Result<Unit> {
        return syncQueueService.deleteTask(id)
    }
    
    override suspend fun retryHistoryItem(id: String): Result<Unit> {
        // Retry uses the existing retryFailedTasks API
        // For v1, we can retry all failed tasks as the API doesn't support single task retry
        return syncQueueService.retryFailedTasks()
            .map { Unit }
    }
}
