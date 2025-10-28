package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.core.sync_queue.api.SyncQueueService
import com.gpcasiapac.storesystems.core.sync_queue.api.model.CollectTaskMetadata
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskType
import java.util.UUID

/**
 * Use case to submit a collect order with full metadata to sync queue.
 * Accepts only the invoice number and fetches the full order data internally.
 */
class SubmitOrderUseCase(
    private val syncQueueService: SyncQueueService,
    private val getOrderByInvoiceNumberUseCase: GetOrderByInvoiceNumberUseCase
) {
    /**
     * Submit order by invoice number.
     * Fetches the full order data and creates a CollectTaskMetadata entry for the sync queue.
     * This metadata will be visible in history.
     */
    suspend operator fun invoke(invoiceNumber: String): Result<Unit> {
        // Fetch the full order data by invoice number
        val orderWithCustomer = getOrderByInvoiceNumberUseCase(invoiceNumber)
            .getOrElse { return Result.failure(it) }
        
        val metadata = CollectTaskMetadata(
            id = UUID.randomUUID().toString(),
            syncTaskId = "", // Will be set by service
            
            // Order fields
            invoiceNumber = orderWithCustomer.order.invoiceNumber,
            salesOrderNumber = orderWithCustomer.order.salesOrderNumber,
            webOrderNumber = orderWithCustomer.order.webOrderNumber,
            orderCreatedAt = orderWithCustomer.order.createdAt,
            orderPickedAt = orderWithCustomer.order.pickedAt,
            
            // Customer fields
            customerNumber = orderWithCustomer.customer.customerNumber,
            customerType = orderWithCustomer.customer.customerType.name,
            accountName = orderWithCustomer.customer.accountName,
            firstName = orderWithCustomer.customer.firstName,
            lastName = orderWithCustomer.customer.lastName,
            phone = orderWithCustomer.customer.phone
        )
        
        return syncQueueService.enqueueCollectTask(
            taskType = TaskType.COLLECT_SUBMIT_ORDER,
            taskId = invoiceNumber,
            priority = 10,
            maxAttempts = 3,
            metadata = metadata
        ).map { Unit }
    }
}