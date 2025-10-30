package com.gpcasiapac.storesystems.feature.history.data.mapper

import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTaskWithCollectMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItemWithMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryType
import kotlin.time.ExperimentalTime

/**
 * Extension function to map SyncTaskWithCollectMetadata to HistoryItemWithMetadata.
 * Builds a metadataList of sealed HistoryMetadata entries.
 * Current API exposes at most one CollectMetadata; wrap it into a list if present.
 */
@OptIn(ExperimentalTime::class)
fun SyncTaskWithCollectMetadata.toHistoryItemWithMetadata(): HistoryItemWithMetadata {
    val metadataList: List<HistoryMetadata> = collectMetadata.map { collectMeta ->
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
        metadataList = metadataList
    )
}

/**
 * Extension function to map list of SyncTaskWithCollectMetadata to HistoryItemWithMetadata.
 */
fun List<SyncTaskWithCollectMetadata>.toHistoryItemsWithMetadata(): List<HistoryItemWithMetadata> {
    return map { it.toHistoryItemWithMetadata() }
}
