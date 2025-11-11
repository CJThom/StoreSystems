package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.core.sync_queue.api.SyncQueueService
import com.gpcasiapac.storesystems.core.sync_queue.api.model.CollectTaskMetadata
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskType
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.CollectUserPrefsRepository
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.GetWorkOrderItemsSnapshotUseCase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.lastOrNull
import java.util.UUID

/**
 * Submit the given Work Order as a single batched Collect task.
 * Uses OrderRepository (indirectly) via GetWorkOrderItemsSnapshotUseCase.
 */
class SubmitOrderUseCase(
    private val syncQueueService: SyncQueueService,
    private val getWorkOrderItemsSnapshotUseCase: GetWorkOrderItemsSnapshotUseCase,
    private val observeCollectWorkOrderUseCase: ObserveCollectWorkOrderUseCase

) {
    /**
     * Batch submit all items belonging to the Work Order.
     */
    suspend operator fun invoke(workOrderId: WorkOrderId): Result<Unit> {
        val items = getWorkOrderItemsSnapshotUseCase(workOrderId)
            .getOrElse { return Result.failure(it) }

        if (items.isEmpty()) return Result.failure(IllegalStateException("No items to submit for workOrderId=$workOrderId"))

        val metadataList = items.map { owc ->
            val order = owc.order
            val customer = owc.customer
            CollectTaskMetadata(
                id = UUID.randomUUID().toString(),
                syncTaskId = "", // Will be set by service

                // Order fields
                invoiceNumber = order.invoiceNumber.value,
                orderNumber = order.orderNumber,
                webOrderNumber = order.webOrderNumber,
                createdDateTime = order.createdDateTime,
                invoiceDateTime = order.invoiceDateTime,

                // Customer fields
                customerNumber = customer.number,
                customerType = customer.customerType.name,
                name = customer.name,
                phone = customer.phone.orEmpty()
            )
        }
        val workOrderSnapshot =
            observeCollectWorkOrderUseCase(workOrderId).firstOrNull() ?: return Result.failure(
                IllegalStateException("Work order not found: $workOrderId")
            )
        val submittedBy = workOrderSnapshot.userId.value

        return syncQueueService.enqueueCollectTask(
            taskType = TaskType.COLLECT_SUBMIT_ORDER,
            taskId = workOrderId.value, // One SyncTask per Work Order
            priority = 10,
            maxAttempts = 3,
            metadata = metadataList,
            submittedBy = submittedBy
        ).map { Unit }
    }
}