package com.gpcasiapac.storesystems.feature.history.domain.usecase

import com.gpcasiapac.storesystems.core.sync_queue.api.SyncQueueService
import com.gpcasiapac.storesystems.feature.history.api.HistoryType
import com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime

/**
 * Observe a single HistoryItem by type + id as a Flow.
 *
 * Minimal implementation: wraps the existing one-shot getter and emits a single value if present.
 * Can be extended later to truly observe repository changes per type.
 */
@OptIn(ExperimentalTime::class)
class ObserveCollectHistoryItemByIdUseCase(
    private val syncQueueService: SyncQueueService
) {
    suspend operator fun invoke(id: String): Flow<HistoryItem> {
        return syncQueueService.observeTasksWithCollectMetadataByTaskIdFlow(entityId = id)
            .map { taskWithMeta ->
                taskWithMeta.let { t ->
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
                        lastError = t.task.errorAttempts.lastOrNull()?.errorMessage,
                        priority = t.task.priority,
                        submittedBy = t.task.submittedBy,
                        requestId = t.task.requestId,
                        metadata = metadata
                    )
                }
            }
    }
}
