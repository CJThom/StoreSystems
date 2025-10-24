package com.gpcasiapac.storesystems.core.sync_queue.domain

import com.gpcasiapac.storesystems.core.sync_queue.api.SyncQueueService
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTask
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskType
import com.gpcasiapac.storesystems.core.sync_queue.domain.repository.SyncRepository
import com.gpcasiapac.storesystems.core.sync_queue.domain.usecase.AddTaskAndTriggerSyncUseCase
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of SyncQueueService using internal domain logic.
 */
internal class SyncQueueServiceImpl(
    private val addTaskAndTriggerSyncUseCase: AddTaskAndTriggerSyncUseCase,
    private val syncRepository: SyncRepository
) : SyncQueueService {
    
    override suspend fun addTaskAndTriggerSync(
        taskType: TaskType,
        entityId: String,
        priority: Int
    ): Result<String> {
        return addTaskAndTriggerSyncUseCase(
            taskType = taskType,
            entityId = entityId,
            priority = priority
        )
    }
    
    override fun observePendingTasksCount(taskType: TaskType): Flow<Int> {
        return syncRepository.observePendingTasksCount(taskType)
    }
    
    override fun observePendingTasks(): Flow<List<SyncTask>> {
        return syncRepository.observePendingTasks()
    }
    
    override fun observeAllTasks(): Flow<List<SyncTask>> {
        return syncRepository.observeAllTasks()
    }
    
    override suspend fun deleteTask(taskId: String): Result<Unit> {
        return syncRepository.deleteTask(taskId)
    }
    
    override suspend fun retryFailedTasks(taskType: TaskType?): Result<Int> {
        return syncRepository.resetFailedTasks(taskType)
    }
    
    override suspend fun getTasksByEntityId(entityId: String): List<SyncTask> {
        return syncRepository.getTasksByEntityId(entityId)
    }
}
