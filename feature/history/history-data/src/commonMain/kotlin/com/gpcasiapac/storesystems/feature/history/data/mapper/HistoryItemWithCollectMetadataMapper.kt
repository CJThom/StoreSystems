package com.gpcasiapac.storesystems.feature.history.data.mapper

import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTaskWithCollectMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItemWithCollectMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryType
import kotlin.time.ExperimentalTime

/**
 * Map SyncTaskWithCollectMetadata (API model) to HistoryItemWithCollectMetadata (Domain model).
 * Returns null if the task doesn't have collect metadata.
 */
@OptIn(ExperimentalTime::class)
fun SyncTaskWithCollectMetadata.toHistoryItemWithCollectMetadata(): HistoryItemWithCollectMetadata? {
    val metadata = collectMetadata.firstOrNull() ?: return null
    
    return HistoryItemWithCollectMetadata(
        // Base history fields from SyncTask
        id = task.id,
        type = HistoryType.fromTaskTypeName(task.taskType.name),
        entityId = task.taskId,
        status = HistoryStatus.fromTaskStatusName(task.status.name),
        timestamp = task.updatedTime,
        attempts = task.noOfAttempts,
        lastError = task.errorAttempts.lastOrNull()?.errorMessage,
        priority = task.priority,
        
        // Collect metadata fields (use first element for deprecated model)
        invoiceNumber = metadata.invoiceNumber,
        salesOrderNumber = metadata.salesOrderNumber,
        webOrderNumber = metadata.webOrderNumber,
        orderCreatedAt = metadata.orderCreatedAt,
        orderPickedAt = metadata.orderPickedAt,
        customerNumber = metadata.customerNumber,
        customerType = metadata.customerType,
        accountName = metadata.accountName,
        firstName = metadata.firstName,
        lastName = metadata.lastName,
        phone = metadata.phone
    )
}

/**
 * Map list of SyncTaskWithCollectMetadata to HistoryItemWithCollectMetadata.
 * Filters out tasks without metadata.
 */
fun List<SyncTaskWithCollectMetadata>.toHistoryItemsWithCollectMetadata(): List<HistoryItemWithCollectMetadata> {
    return mapNotNull { it.toHistoryItemWithCollectMetadata() }
}
