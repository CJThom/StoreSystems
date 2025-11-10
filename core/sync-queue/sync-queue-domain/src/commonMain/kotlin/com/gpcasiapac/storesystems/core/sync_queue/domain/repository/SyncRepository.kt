package com.gpcasiapac.storesystems.core.sync_queue.domain.repository

import com.gpcasiapac.storesystems.core.sync_queue.api.model.CollectTaskMetadata
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTask
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTaskAttemptError
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTaskWithCollectMetadata
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskStatus
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskType
import kotlinx.coroutines.flow.Flow

interface SyncRepository {
    /** Add a new task to the sync queue */
    suspend fun addTask(taskType: TaskType, taskId: String, priority: Int = 0): Result<String>
    
    /** Get tasks by type and status for processing */
    suspend fun getTasksByType(taskType: TaskType, status: TaskStatus = TaskStatus.PENDING, limit: Int = 10): List<SyncTask>
    
    /** Get next pending task with highest priority */
    suspend fun getNextPendingTask(): SyncTask?
    
    /** Atomically mark task IN_PROGRESS and increment attempts; returns the updated attempt number, or null if not started. */
    suspend fun startAttempt(taskId: String): Int?
    
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
    
    /** Observe all tasks (pending, completed, failed) */
    fun observeAllTasks(): Flow<List<SyncTask>>
    
    /** Delete a specific task */
    suspend fun deleteTask(taskId: String): Result<Unit>
    
    /** Reset failed tasks back to pending (for retry scenarios) */
    suspend fun resetFailedTasks(taskType: TaskType? = null): Result<Int>
    
    /** Get tasks by the actual entity ID they reference */
    suspend fun getTasksByEntityId(entityId: String): List<SyncTask>
    
    /** Observe all tasks with collect metadata */
    fun observeAllTasksWithCollectMetadata(): Flow<List<SyncTaskWithCollectMetadata>>
    
    /** Get task with collect metadata by ID */
    suspend fun getTaskWithCollectMetadata(taskId: String): Result<SyncTaskWithCollectMetadata?>
    
    /** Get tasks by entity ID with collect metadata */
    suspend fun getTasksWithCollectMetadataByEntityId(entityId: String): List<SyncTaskWithCollectMetadata>

    suspend fun observeTasksWithCollectMetadataByTaskIdFlow(entityId: String): Flow<SyncTaskWithCollectMetadata>


    /** Get tasks by invoice number */
    suspend fun getTasksByInvoiceNumber(invoiceNumber: String): List<SyncTaskWithCollectMetadata>
    
    /** Get tasks by customer number */
    suspend fun getTasksByCustomerNumber(customerNumber: String): List<SyncTaskWithCollectMetadata>
    
    /** Reset a single task for manual retry; if resetAttempts is true, attempts and errors are cleared. Optionally bump maxAttempts to allow retry without wiping history. */
    suspend fun resetTaskForRetry(
        taskId: String,
        resetAttempts: Boolean = false,
        bumpMaxAttemptsBy: Int = 0
    ): Result<Unit>
    
    /** Enqueue a collect task with metadata list */
    suspend fun enqueueCollectTask(
        taskType: TaskType,
        taskId: String,
        priority: Int = 0,
        maxAttempts: Int = 3,
        metadata: List<CollectTaskMetadata>,
        submittedBy: String? = null
    ): Result<String>
}