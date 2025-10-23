package com.gpcasiapac.storesystems.core.sync_queue.api

import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTask
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
     * Delete a specific task by ID.
     */
    suspend fun deleteTask(taskId: String): Result<Unit>
    
    /**
     * Retry failed tasks for a specific type (or all if null).
     */
    suspend fun retryFailedTasks(taskType: TaskType? = null): Result<Int>
    
    /**
     * Get tasks for a specific entity ID.
     */
    suspend fun getTasksByEntityId(entityId: String): List<SyncTask>
}
