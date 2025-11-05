package com.gpcasiapac.storesystems.feature.collect.domain.sync

import com.gpcasiapac.storesystems.core.sync_queue.api.SyncHandler
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTask
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskType
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * SyncHandler for submitting a work order created by the Collect feature.
 * For v1 we simply delegate to OrderRepository and return its Result.
 */
class SubmitOrderSyncHandler(
    private val orderLocalRepository: OrderLocalRepository
) : SyncHandler {
    override val supportedTypes: Set<TaskType> = setOf(TaskType.COLLECT_SUBMIT_ORDER)

    override suspend fun handle(task: SyncTask): Result<Unit> {
        // task.taskId represents the workOrderId in the sync queue
        // TODO: Implement actual submission when OrderRepository.submitWorkOrder() is added
        delay(8000)
        return if (Random.nextInt(2, 4) % 2 == 0) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalStateException("There was an error submitting the order: ${task.taskId}, retry later."))
        }
    }
}
