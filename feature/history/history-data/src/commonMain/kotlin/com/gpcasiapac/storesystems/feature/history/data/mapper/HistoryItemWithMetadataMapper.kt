package com.gpcasiapac.storesystems.feature.history.data.mapper

import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTaskWithCollectMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItemWithMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryType
import kotlin.time.ExperimentalTime

/**
 * Extension function to map SyncTaskWithCollectMetadata to HistoryItemWithMetadata.
 * Maps collect metadata to sealed HistoryMetadata.CollectMetadata if available,
 * otherwise uses HistoryMetadata.NoMetadata.
 */
@OptIn(ExperimentalTime::class)
fun SyncTaskWithCollectMetadata.toHistoryItemWithMetadata(): HistoryItemWithMetadata {
    val collectMeta = collectMetadata
    val metadata = if (collectMeta != null) {
        HistoryMetadata.CollectMetadata(
            invoiceNumber = collectMeta.invoiceNumber,
            salesOrderNumber = collectMeta.salesOrderNumber,
            webOrderNumber = collectMeta.webOrderNumber,
            orderCreatedAt = collectMeta.orderCreatedAt,
            orderPickedAt = collectMeta.orderPickedAt,
            customerNumber = collectMeta.customerNumber,
            customerType = collectMeta.customerType,
            accountName = collectMeta.accountName,
            firstName = collectMeta.firstName,
            lastName = collectMeta.lastName,
            phone = collectMeta.phone
        )
    } else {
        HistoryMetadata.NoMetadata
    }
    
    return HistoryItemWithMetadata(
        id = task.id,
        type = HistoryType.fromTaskTypeName(task.taskType.name),
        entityId = task.taskId,
        status = HistoryStatus.fromTaskStatusName(task.status.name),
        timestamp = task.updatedTime,
        attempts = task.noOfAttempts,
        lastError = task.errorAttempts.lastOrNull()?.errorMessage,
        priority = task.priority,
        metadata = metadata
    )
}

/**
 * Extension function to map list of SyncTaskWithCollectMetadata to HistoryItemWithMetadata.
 */
fun List<SyncTaskWithCollectMetadata>.toHistoryItemsWithMetadata(): List<HistoryItemWithMetadata> {
    return map { it.toHistoryItemWithMetadata() }
}
