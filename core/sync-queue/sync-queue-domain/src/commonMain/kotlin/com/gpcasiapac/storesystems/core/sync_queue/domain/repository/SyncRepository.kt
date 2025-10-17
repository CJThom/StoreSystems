package com.gpcasiapac.storesystems.core.sync_queue.domain.repository

import com.gpcasiapac.storesystems.core.sync_queue.domain.model.SyncTask
import com.gpcasiapac.storesystems.core.sync_queue.domain.model.SyncTaskAttemptError
import com.gpcasiapac.storesystems.core.sync_queue.domain.model.TaskStatus
import com.gpcasiapac.storesystems.core.sync_queue.domain.model.TaskType
import kotlinx.coroutines.flow.Flow

interface SyncRepository {
    /** Add a new task to the sync queue */
    suspend fun addTask(taskType: TaskType, taskId: String, priority: Int = 0): Result<String>
    
    /** Get tasks by type and status for processing */
    suspend fun getTasksByType(taskType: TaskType, status: TaskStatus = TaskStatus.PENDING, limit: Int = 10): List<SyncTask>
    
    /** Get next pending task with highest priority */
    suspend fun getNextPendingTask(): SyncTask?
    
    /** Update task status with optional error attempt */
    suspend fun updateTaskStatus(taskId: String, status: TaskStatus, errorAttempt: SyncTaskAttemptError? = null): Result<Unit>
    
    /** Increment attempt count and update last attempt time */
    suspend fun incrementTaskAttempt(taskId: String): Result<Unit>
    
    /** Remove completed or failed tasks older than specified days */
    suspend fun cleanupOldTasks(olderThanDays: Int = 7): Result<Int>
    
    /** Observe pending tasks count by type */
    fun observePendingTasksCount(taskType: TaskType): Flow<Int>
    
    /** Observe all pending tasks */
    fun observePendingTasks(): Flow<List<SyncTask>>
    
    /** Delete a specific task */
    suspend fun deleteTask(taskId: String): Result<Unit>
    
    /** Reset failed tasks back to pending (for retry scenarios) */
    suspend fun resetFailedTasks(taskType: TaskType? = null): Result<Int>
    
    /** Get tasks by the actual entity ID they reference */
    suspend fun getTasksByEntityId(entityId: String): List<SyncTask>
}