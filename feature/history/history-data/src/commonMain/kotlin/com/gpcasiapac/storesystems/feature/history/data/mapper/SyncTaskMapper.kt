package com.gpcasiapac.storesystems.feature.history.data.mapper

import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTask
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryType
import kotlin.time.ExperimentalTime

/**
 * Extension function to map SyncTask to HistoryItem.
 */
@OptIn(ExperimentalTime::class)
fun SyncTask.toHistoryItem(): HistoryItem {
    return HistoryItem(
        id = id,
        type = HistoryType.fromTaskTypeName(taskType.name),
        entityId = taskId,
        status = HistoryStatus.fromTaskStatusName(status.name),
        timestamp = updatedTime,
        attempts = noOfAttempts,
        lastError = errorAttempts.lastOrNull()?.errorMessage,
        priority = priority
    )
}

/**
 * Extension function to map list of SyncTasks to HistoryItems.
 */
fun List<SyncTask>.toHistoryItems(): List<HistoryItem> {
    return map { it.toHistoryItem() }
}
