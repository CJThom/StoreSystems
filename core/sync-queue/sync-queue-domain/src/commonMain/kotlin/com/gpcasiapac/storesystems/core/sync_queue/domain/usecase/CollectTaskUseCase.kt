package com.gpcasiapac.storesystems.core.sync_queue.domain.usecase

import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTask
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTaskAttemptError
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskStatus
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskType
import com.gpcasiapac.storesystems.core.sync_queue.domain.repository.SyncRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class CollectTaskUseCase(
    private val syncRepository: SyncRepository
) {
    /** Collect tasks by specific type */
    suspend fun collectByType(
        taskType: TaskType,
        limit: Int = 10
    ): List<SyncTask> {
        return syncRepository.getTasksByType(
            taskType = taskType,
            status = TaskStatus.PENDING,
            limit = limit
        )
    }
    
    /** Collect next highest priority task */
    suspend fun collectNext(): SyncTask? {
        return syncRepository.getNextPendingTask()
    }
    
    /** Mark task as in progress */
    suspend fun markInProgress(taskId: String): Result<Unit> {
        return syncRepository.updateTaskStatus(taskId, TaskStatus.IN_PROGRESS)
    }
    
    /** Mark task as completed */
    suspend fun markCompleted(taskId: String): Result<Unit> {
        return syncRepository.updateTaskStatus(taskId, TaskStatus.COMPLETED)
    }
    
    /** Mark task as failed with error attempt */
    suspend fun markFailed(taskId: String, errorMessage: String, attemptNumber: Int): Result<Unit> {
        val errorAttempt = SyncTaskAttemptError(attemptNumber, Clock.System.now(), errorMessage)
        return syncRepository.updateTaskStatus(taskId, TaskStatus.FAILED, errorAttempt)
    }
}