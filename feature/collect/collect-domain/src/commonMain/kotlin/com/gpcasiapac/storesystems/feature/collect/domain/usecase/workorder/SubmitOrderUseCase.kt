package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.core.sync_queue.api.SyncQueueService
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskType

class SubmitOrderUseCase(
    private val syncQueueService: SyncQueueService
) {
    suspend operator fun invoke(workOrderId: String): Result<Unit> {
        return syncQueueService.addTaskAndTriggerSync(
            taskType = TaskType.COLLECT_SUBMIT_ORDER,
            entityId = workOrderId,
        ).map {
            Unit
        }
    }
}