package com.gpcasiapac.storesystems.core.sync_queue.api

import com.gpcasiapac.storesystems.core.sync_queue.api.model.CollectTaskMetadata
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTask
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTaskWithCollectMetadata
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskType
import kotlinx.coroutines.flow.Flow

/**
 * Public facade for sync queue operations.
 * Implemented in sync-queue-domain using internal use cases.
 */
interface SyncQueueService {
    /**
     * Add a task to the sync queue and trigger platform-specific sync.
     * Returns the task ID on success.
     */
    suspend fun addTaskAndTriggerSync(
        taskType: TaskType,
        entityId: String,
        priority: Int = 0
    ): Result<String>
    
    /**
     * Observe pending tasks count for a specific type.
     */
    fun observePendingTasksCount(taskType: TaskType): Flow<Int>
    
    /**
     * Observe all pending tasks.
     */
    fun observePendingTasks(): Flow<List<SyncTask>>
    
    /**
     * Observe all tasks regardless of status.
     */
    fun observeAllTasks(): Flow<List<SyncTask>>
    
    /**
     * Delete a specific task by ID.
     */
    suspend fun deleteTask(taskId: String): Result<Unit>
    
    /**
     * Retry failed tasks for a specific type (or all if null).
     */
    suspend fun retryFailedTasks(taskType: TaskType? = null): Result<Int>
    
    /**
     * Retry a single task by id: typically resets status to PENDING.
     */
    suspend fun retryTask(taskId: String): Result<Unit>
    
    /**
     * Get tasks for a specific entity ID.
     */
    suspend fun getTasksByEntityId(entityId: String): List<SyncTask>
    
    /**
     * Observe all tasks with collect metadata.
     */
    fun observeAllTasksWithCollectMetadata(): Flow<List<SyncTaskWithCollectMetadata>>
    
    /**
     * Get task with collect metadata by ID.
     */
    suspend fun getTaskWithCollectMetadata(taskId: String): Result<SyncTaskWithCollectMetadata?>
    
    /**
     * Get tasks by entity ID with collect metadata.
     */
    suspend fun getTasksWithCollectMetadataByEntityId(entityId: String): List<SyncTaskWithCollectMetadata>

    suspend fun observeTasksWithCollectMetadataByTaskIdFlow(entityId: String): Flow<SyncTaskWithCollectMetadata>


    /**
     * Get tasks by invoice number.
     */
    suspend fun getTasksByInvoiceNumber(invoiceNumber: String): List<SyncTaskWithCollectMetadata>
    
    /**
     * Get tasks by customer number.
     */
    suspend fun getTasksByCustomerNumber(customerNumber: String): List<SyncTaskWithCollectMetadata>
    
    /**
     * Enqueue a collect task with multiple metadata rows.
     */
    suspend fun enqueueCollectTask(
        taskType: TaskType,
        taskId: String,
        priority: Int = 0,
        maxAttempts: Int = 3,
        metadata: List<CollectTaskMetadata>,
        submittedBy: String? = null
    ): Result<String>
}
