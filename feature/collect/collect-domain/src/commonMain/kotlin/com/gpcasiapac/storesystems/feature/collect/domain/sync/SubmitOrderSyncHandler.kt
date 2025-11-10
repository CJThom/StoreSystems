package com.gpcasiapac.storesystems.feature.collect.domain.sync

import com.gpcasiapac.storesystems.common.kotlin.extension.toLocalDateTimeString
import com.gpcasiapac.storesystems.core.sync_queue.api.SyncHandler
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTask
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskType
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerSignatureRequest
import com.gpcasiapac.storesystems.feature.collect.domain.model.SubmitWorkOrderRequest
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRemoteRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlin.time.Clock

/**
 * SyncHandler for submitting a work order created by the Collect feature.
 * Sticks to the SyncHandler contract and fetches required details from OrderLocalRepository.
 */
class SubmitOrderSyncHandler(
    private val orderLocalRepository: OrderLocalRepository,
    private val orderRemoteRepository: OrderRemoteRepository,
) : SyncHandler {
    override val supportedTypes: Set<TaskType> = setOf(TaskType.COLLECT_SUBMIT_ORDER)

    override suspend fun handle(task: SyncTask): Result<Unit> {
        //Delay
        delay(5000)
        // task.taskId represents the workOrderId in the sync queue
        val workOrderId = WorkOrderId(task.taskId)

        // Fetch snapshot from local repository
        val snapshot = orderLocalRepository.getWorkOrderByIdSnapshot(workOrderId)
            ?: return Result.failure(IllegalStateException("1. Work order not found: ${task.taskId}"))

        val signature = orderLocalRepository.getWorkOrderSignatureFlow(workOrderId).firstOrNull()
            ?: return Result.failure(
                IllegalStateException("2. Work order signature not found: ${task.taskId}")
            )
        // Derive fields
        val first = snapshot.collectOrderWithCustomerList.firstOrNull()
            ?: return Result.failure(IllegalStateException("No items to submit for workOrderId=${task.taskId}"))

        val orderChannel = when (first.customer.customerType.name.uppercase()) {
            "B2B" -> "B2B"
            else -> "B2C"
        }

        val invoices =
            snapshot.collectOrderWithCustomerList.map { it.order.invoiceNumber.value }.distinct()
        val signerName = first.customer.name
        val submitter = task.submittedBy.orEmpty()

        val request = SubmitWorkOrderRequest(
            id = task.requestId,
            orderChannel = orderChannel,
            customerSignature = CustomerSignatureRequest(
                signature = signature.signatureBase64,
                name = signature.signedByName ?: signerName,
                signatureAt = signature.signedAt.toLocalDateTimeString()
            ),
            courierName = snapshot.collectWorkOrder.courierName.orEmpty(),
            submitTimestamp = Clock.System.now().toLocalDateTimeString(),
            repId = submitter,
            invoices = invoices
        )
        return orderRemoteRepository.submitWorkOrder(request)
    }
}
