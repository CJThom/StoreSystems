package com.gpcasiapac.storesystems.core.sync_queue.domain.usecase

import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskType
import com.gpcasiapac.storesystems.core.sync_queue.domain.repository.SyncRepository

class AddTaskToQueueUseCase(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(
        taskType: TaskType,
        entityId: String,
        priority: Int = 0
    ): Result<String> {
        return syncRepository.addTask(
            taskType = taskType,
            taskId = entityId,
            priority = priority
        )
    }
}