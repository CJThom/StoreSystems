package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.core.sync_queue.api.SyncQueueService
import com.gpcasiapac.storesystems.core.sync_queue.api.model.CollectTaskMetadata
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskType
import java.util.UUID

/**
 * Submit the given Work Order as a single batched Collect task.
 * Uses OrderRepository (indirectly) via GetWorkOrderItemsSnapshotUseCase.
 */
class SubmitOrderUseCase(
    private val syncQueueService: SyncQueueService,
    private val getWorkOrderItemsSnapshotUseCase: GetWorkOrderItemsSnapshotUseCase,
) {
    /**
     * Batch submit all items belonging to the Work Order.
     */
    suspend operator fun invoke(workOrderId: String): Result<Unit> {
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
                invoiceNumber = order.invoiceNumber,
                salesOrderNumber = order.salesOrderNumber,
                webOrderNumber = order.webOrderNumber,
                orderCreatedAt = order.createdAt,
                orderPickedAt = order.pickedAt,

                // Customer fields
                customerNumber = customer.customerNumber,
                customerType = customer.customerType.name,
                accountName = customer.accountName,
                firstName = customer.firstName,
                lastName = customer.lastName,
                phone = customer.phone
            )
        }

        return syncQueueService.enqueueCollectTask(
            taskType = TaskType.COLLECT_SUBMIT_ORDER,
            taskId = workOrderId, // One SyncTask per Work Order
            priority = 10,
            maxAttempts = 3,
            metadata = metadataList
        ).map { Unit }
    }
}