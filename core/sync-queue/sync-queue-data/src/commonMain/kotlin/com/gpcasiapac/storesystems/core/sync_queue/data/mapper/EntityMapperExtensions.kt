package com.gpcasiapac.storesystems.core.sync_queue.data.mapper

import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.entity.SyncTaskEntity
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTask
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskStatus
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskType
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun SyncTaskEntity.toDomain(): SyncTask {
    return SyncTask(
        id = id,
        taskType = TaskType.valueOf(taskType),
        status = TaskStatus.valueOf(status),
        taskId = taskId,
        noOfAttempts = noOfAttempts,
        maxAttempts = maxAttempts,
        priority = priority,
        addedTime = addedTime,
        updatedTime = updatedTime,
        lastAttemptTime = lastAttemptTime,
        lastError = lastError,
        submittedBy = submittedBy,
        requestId = requestId
    )
}
