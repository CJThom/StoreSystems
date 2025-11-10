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
                    orderNumber = cm.orderNumber,
                    webOrderNumber = cm.webOrderNumber,
                    createdDateTime = cm.createdDateTime,
                    invoiceDateTime = cm.invoiceDateTime,
                    customerNumber = cm.customerNumber,
                    customerType = cm.customerType,
                    name = cm.name,
                    phone = cm.phone
                )
            }
            CollectHistoryItem(
                id = t.task.id,
                entityId = t.task.taskId,
                status = HistoryStatus.fromTaskStatusName(t.task.status.name),
                timestamp = t.task.updatedTime,
                attempts = t.task.noOfAttempts,
                lastError = t.task.lastError,
                priority = t.task.priority,
                submittedBy = t.task.submittedBy,
                requestId = t.task.requestId,
                metadata = metadata
            )
        }
    }
}