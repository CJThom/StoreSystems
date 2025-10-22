package com.gpcasiapac.storesystems.feature.collect.domain.sync

import com.gpcasiapac.storesystems.core.sync_queue.domain.SyncHandler
import com.gpcasiapac.storesystems.core.sync_queue.domain.model.SyncTask
import com.gpcasiapac.storesystems.core.sync_queue.domain.model.TaskType
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

/**
 * SyncHandler for submitting a work order created by the Collect feature.
 * For v1 we simply delegate to OrderRepository and return its Result.
 */
class SubmitOrderSyncHandler(
    private val orderRepository: OrderRepository
) : SyncHandler {
    override val supportedTypes: Set<TaskType> = setOf(TaskType.COLLECT_SUBMIT_ORDER)

    override suspend fun handle(task: SyncTask): Result<Unit> {
        // task.taskId represents the workOrderId in the sync queue
        // TODO: Implement actual submission when OrderRepository.submitWorkOrder() is added
        return Result.success(Unit)
    }
}
