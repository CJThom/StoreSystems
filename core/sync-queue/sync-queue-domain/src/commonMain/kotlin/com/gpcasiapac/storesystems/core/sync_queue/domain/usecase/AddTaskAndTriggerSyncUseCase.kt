package com.gpcasiapac.storesystems.core.sync_queue.domain.usecase

import com.gpcasiapac.storesystems.core.sync_queue.api.coordinator.SyncTriggerCoordinator
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskType
import com.gpcasiapac.storesystems.core.sync_queue.domain.repository.SyncRepository

/**
 * Adds a task to the sync queue and triggers platform-specific sync mechanism.
 * This is a common (multiplatform) use case that delegates platform-specific
 * triggering to SyncTriggerCoordinator.
 */
class AddTaskAndTriggerSyncUseCase(
    private val syncRepository: SyncRepository,
    private val syncTriggerCoordinator: SyncTriggerCoordinator
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
        ).onSuccess {
            // Trigger platform-specific sync after task is added
            syncTriggerCoordinator.triggerSync()
        }
    }
}
