package com.gpcasiapac.storesystems.feature.history.domain.usecase

import com.gpcasiapac.storesystems.core.sync_queue.api.SyncQueueService
import com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus

/**
 * Loads a single Collect history item by its task id from SyncQueueService and maps it
 * to the domain HistoryItem used by presentation.
 */
class GetCollectHistoryItemByIdUseCase(
    private val syncQueueService: SyncQueueService
) {
    @OptIn(kotlin.time.ExperimentalTime::class)
    suspend operator fun invoke(id: String): Result<HistoryItem?> = runCatching {
        val res = syncQueueService.getTaskWithCollectMetadata(id)
        val taskWithMeta = res.getOrNull()
        taskWithMeta?.let { t ->
            val metadata = t.collectMetadata.map { cm ->
                HistoryMetadata.CollectMetadata(
                    invoiceNumber = cm.invoiceNumber,
                    salesOrderNumber = cm.salesOrderNumber,
                    webOrderNumber = cm.webOrderNumber,
                    orderCreatedAt = cm.orderCreatedAt,
                    orderPickedAt = cm.orderPickedAt,
                    customerNumber = cm.customerNumber,
                    customerType = cm.customerType,
                    accountName = cm.accountName,
                    firstName = cm.firstName,
                    lastName = cm.lastName,
                    phone = cm.phone
                )
            }
            CollectHistoryItem(
                id = t.task.id,
                entityId = t.task.taskId,
                status = HistoryStatus.fromTaskStatusName(t.task.status.name),
                timestamp = t.task.updatedTime,
                attempts = t.task.noOfAttempts,
                lastError = t.task.errorAttempts.lastOrNull()?.errorMessage,
                priority = t.task.priority,
                metadata = metadata
            )
        }
    }
}