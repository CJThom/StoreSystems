package com.gpcasiapac.storesystems.feature.history.data.mapper

import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTaskWithCollectMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import kotlin.time.ExperimentalTime

/**
 * Map SyncTaskWithCollectMetadata (API model) to sealed HistoryItem subtype.
 * Currently produces CollectHistoryItem with a list of CollectMetadata entries.
 */
@OptIn(ExperimentalTime::class)
fun SyncTaskWithCollectMetadata.toHistoryItem(): HistoryItem {
    val collects = collectMetadata.map { cm ->
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
    return CollectHistoryItem(
        id = task.id,
        entityId = task.taskId,
        status = HistoryStatus.fromTaskStatusName(task.status.name),
        timestamp = task.updatedTime,
        attempts = task.noOfAttempts,
        lastError = task.errorAttempts.lastOrNull()?.errorMessage,
        priority = task.priority,
        metadata = collects
    )
}

fun List<SyncTaskWithCollectMetadata>.toHistoryItems(): List<HistoryItem> = map { it.toHistoryItem() }
