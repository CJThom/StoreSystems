package com.gpcasiapac.storesystems.core.sync_queue.data.mapper

import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.entity.SyncTaskEntity
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTask
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTaskAttemptError
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskStatus
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskType
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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
        errorAttempts = parseErrorAttempts(errorAttempts)
    )
}

private fun parseErrorAttempts(errorAttemptsJson: String?): List<SyncTaskAttemptError> {
    if (errorAttemptsJson.isNullOrEmpty() || errorAttemptsJson == "[]") return emptyList()
    
    return try {
        val items = mutableListOf<SyncTaskAttemptError>()
        val jsonArray = errorAttemptsJson.trim().removeSurrounding("[", "]")
        
        if (jsonArray.isEmpty()) return emptyList()
        
        // Simple regex-based parsing for the specific structure
        val itemRegex = """"attemptNumber":(\d+),"timestamp":(\d+),"errorMessage":"((?:[^"\\]|\\.)*)"""".toRegex()
        itemRegex.findAll(jsonArray).forEach { match ->
            val attemptNumber = match.groupValues[1].toInt()
            val timestamp = Instant.fromEpochMilliseconds(match.groupValues[2].toLong())
            val errorMessage = match.groupValues[3].replace("\\\"", "\"")
            items.add(SyncTaskAttemptError(attemptNumber, timestamp, errorMessage))
        }
        items
    } catch (e: Exception) {
        emptyList()
    }
}