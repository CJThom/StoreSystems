package com.gpcasiapac.storesystems.feature.history.domain.usecase

import com.gpcasiapac.storesystems.core.sync_queue.api.SyncQueueService

/**
 * Use case to retry a single history task via SyncQueueService.
 */
class RetryHistoryUseCase(
    private val syncQueueService: SyncQueueService
) {
    suspend operator fun invoke(id: String) =
        syncQueueService.retryTask(id)
}