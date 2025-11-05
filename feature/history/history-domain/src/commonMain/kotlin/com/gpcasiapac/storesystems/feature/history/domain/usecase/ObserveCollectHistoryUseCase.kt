package com.gpcasiapac.storesystems.feature.history.domain.usecase

import com.gpcasiapac.storesystems.core.sync_queue.api.SyncQueueService
import com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime

/**
 * Observes Collect history items by consuming SyncQueueService and mapping to sealed HistoryItem.
 */
class ObserveCollectHistoryUseCase(
    private val syncQueueService: SyncQueueService
) {
    @OptIn(ExperimentalTime::class)
    operator fun invoke(): Flow<List<HistoryItem>> =
        syncQueueService.observeAllTasksWithCollectMetadata()
            .map { tasks ->
                tasks.map { t ->
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