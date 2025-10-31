package com.gpcasiapac.storesystems.core.sync_queue.domain

import com.gpcasiapac.storesystems.core.sync_queue.api.SyncQueueService
import com.gpcasiapac.storesystems.core.sync_queue.api.model.CollectTaskMetadata
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTask
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTaskWithCollectMetadata
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskType
import com.gpcasiapac.storesystems.core.sync_queue.domain.repository.SyncRepository
import com.gpcasiapac.storesystems.core.sync_queue.domain.usecase.AddTaskAndTriggerSyncUseCase
import com.gpcasiapac.storesystems.core.sync_queue.domain.usecase.EnqueueCollectTaskAndTriggerSyncUseCase
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of SyncQueueService using internal domain logic.
 */
internal class SyncQueueServiceImpl(
    private val addTaskAndTriggerSyncUseCase: AddTaskAndTriggerSyncUseCase,
    private val enqueueCollectTaskAndTriggerSyncUseCase: EnqueueCollectTaskAndTriggerSyncUseCase,
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
    
    override fun observeAllTasksWithCollectMetadata(): Flow<List<SyncTaskWithCollectMetadata>> {
        return syncRepository.observeAllTasksWithCollectMetadata()
    }
    
    override suspend fun getTaskWithCollectMetadata(taskId: String): Result<SyncTaskWithCollectMetadata?> {
        return syncRepository.getTaskWithCollectMetadata(taskId)
    }
    
    override suspend fun getTasksWithCollectMetadataByEntityId(entityId: String): List<SyncTaskWithCollectMetadata> {
        return syncRepository.getTasksWithCollectMetadataByEntityId(entityId)
    }
    
    override suspend fun getTasksByInvoiceNumber(invoiceNumber: String): List<SyncTaskWithCollectMetadata> {
        return syncRepository.getTasksByInvoiceNumber(invoiceNumber)
    }
    
    override suspend fun getTasksByCustomerNumber(customerNumber: String): List<SyncTaskWithCollectMetadata> {
        return syncRepository.getTasksByCustomerNumber(customerNumber)
    }
    
    override suspend fun enqueueCollectTask(
        taskType: TaskType,
        taskId: String,
        priority: Int,
        maxAttempts: Int,
        metadata: List<CollectTaskMetadata>
    ): Result<String> {
        return enqueueCollectTaskAndTriggerSyncUseCase(
            taskType = taskType,
            taskId = taskId,
            priority = priority,
            maxAttempts = maxAttempts,
            metadata = metadata
        )
    }
}
