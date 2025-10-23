package com.gpcasiapac.storesystems.core.sync_queue.data.repository

import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.dao.SyncTaskDao
import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.entity.SyncTaskEntity
import com.gpcasiapac.storesystems.core.sync_queue.data.mapper.toDomain
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTask
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTaskAttemptError
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskStatus
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskType
import com.gpcasiapac.storesystems.core.sync_queue.domain.repository.SyncRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import java.util.UUID

@OptIn(ExperimentalTime::class)
class SyncRepositoryImpl(
    private val syncTaskDao: SyncTaskDao
) : SyncRepository {

    override suspend fun addTask(taskType: TaskType, taskId: String, priority: Int): Result<String> = runCatching {
        val syncTaskId = UUID.randomUUID().toString()
        val now = Clock.System.now()
        
        val task = SyncTaskEntity(
            id = syncTaskId,
            taskType = taskType.name,
            status = TaskStatus.PENDING.name,
            taskId = taskId,
            noOfAttempts = 0,
            maxAttempts = 3,
            priority = priority,
            addedTime = now,
            updatedTime = now,
            lastAttemptTime = null,
            errorAttempts = null
        )
        
        syncTaskDao.insertTask(task)
        syncTaskId
    }

    override suspend fun getTasksByType(taskType: TaskType, status: TaskStatus, limit: Int): List<SyncTask> {
        return syncTaskDao.getTasksByTypeAndStatus(taskType.name, status.name, limit).map { it.toDomain() }
    }

    override suspend fun getNextPendingTask(): SyncTask? {
        return syncTaskDao.getNextPendingTask()?.toDomain()
    }

    override suspend fun updateTaskStatus(
        taskId: String,
        status: TaskStatus,
        errorAttempt: SyncTaskAttemptError?
    ): Result<Unit> = runCatching {
        val now = Clock.System.now()
        val currentTask = syncTaskDao.getTaskById(taskId)
        
        if (currentTask != null) {
            val newErrorAttemptsJson = if (errorAttempt != null) {
                val currentErrors = parseErrorAttemptsFromJson(currentTask.errorAttempts)
                val updatedErrors = currentErrors + errorAttempt
                convertErrorAttemptsToJson(updatedErrors)
            } else {
                currentTask.errorAttempts
            }
            
            val updatedTask = currentTask.copy(
                status = status.name,
                updatedTime = now,
                errorAttempts = newErrorAttemptsJson
            )
            
            syncTaskDao.updateTask(updatedTask)
        }
    }
    
    private fun parseErrorAttemptsFromJson(json: String?): List<SyncTaskAttemptError> {
        if (json.isNullOrEmpty() || json == "[]") return emptyList()
        
        return try {
            val items = mutableListOf<SyncTaskAttemptError>()
            val jsonArray = json.trim().removeSurrounding("[", "]")
            
            if (jsonArray.isEmpty()) return emptyList()
            
            val itemRegex = """"attemptNumber":(\d+),"timestamp":(\d+),"errorMessage":"((?:[^"\\]|\\.)*)"""".toRegex()
            itemRegex.findAll(jsonArray).forEach { match ->
                val attemptNumber = match.groupValues[1].toInt()
                val timestamp = kotlin.time.Instant.fromEpochMilliseconds(match.groupValues[2].toLong())
                val errorMessage = match.groupValues[3].replace("\\\"", "\"")
                items.add(SyncTaskAttemptError(attemptNumber, timestamp, errorMessage))
            }
            items
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun convertErrorAttemptsToJson(errors: List<SyncTaskAttemptError>): String {
        if (errors.isEmpty()) return "[]"
        
        val jsonItems = errors.joinToString(",") { error ->
            """{"attemptNumber":${error.attemptNumber},"timestamp":${error.timestamp.toEpochMilliseconds()},"errorMessage":"${error.errorMessage.replace("\"", "\\\"")}"}"""
        }
        return "[$jsonItems]"
    }

    override suspend fun incrementTaskAttempt(taskId: String): Result<Unit> = runCatching {
        val now = Clock.System.now()
        syncTaskDao.incrementAttempt(taskId, now, now)
    }

    override suspend fun cleanupOldTasks(olderThanDays: Int): Result<Int> = runCatching {
        val cutoffTime = Clock.System.now() - olderThanDays.days
        syncTaskDao.deleteOldTasks(cutoffTime)
    }

    override fun observePendingTasksCount(taskType: TaskType): Flow<Int> {
        return syncTaskDao.observePendingTasksCount(taskType.name)
    }

    override fun observePendingTasks(): Flow<List<SyncTask>> {
        return syncTaskDao.observePendingTasks().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> = runCatching {
        syncTaskDao.deleteTask(taskId)
    }

    override suspend fun resetFailedTasks(taskType: TaskType?): Result<Int> = runCatching {
        syncTaskDao.resetFailedTasks(taskType?.name, Clock.System.now())
    }

    override suspend fun getTasksByEntityId(entityId: String): List<SyncTask> {
        return syncTaskDao.getTasksByEntityId(entityId).map { it.toDomain() }
    }
}