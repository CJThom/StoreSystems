package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.core.sync_queue.api.SyncQueueService
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskType
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId

class SubmitOrderUseCase(
    private val syncQueueService: SyncQueueService
) {
    suspend operator fun invoke(workOrderId: WorkOrderId): Result<Unit> {
        return syncQueueService.addTaskAndTriggerSync(
            taskType = TaskType.COLLECT_SUBMIT_ORDER,
            entityId = workOrderId.value, // TODO: Use WorkOrderId value class?
        ).map {
            Unit
        }
    }
}