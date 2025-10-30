package com.gpcasiapac.storesystems.feature.history.domain.usecase

import com.gpcasiapac.storesystems.core.sync_queue.api.SyncQueueService
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskType

/**
 * Use case to retry failed history tasks via SyncQueueService.
 */
class RetryHistoryUseCase(
    private val syncQueueService: SyncQueueService
) {
    suspend operator fun invoke(taskType: TaskType? = null) =
        syncQueueService.retryFailedTasks(taskType)
}