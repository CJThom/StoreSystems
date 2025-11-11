package com.gpcasiapac.storesystems.core.sync_queue.domain.usecase

import com.gpcasiapac.storesystems.core.sync_queue.api.coordinator.SyncTriggerCoordinator
import com.gpcasiapac.storesystems.core.sync_queue.api.model.CollectTaskMetadata
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskType
import com.gpcasiapac.storesystems.core.sync_queue.domain.repository.SyncRepository

/**
 * Enqueues a collect task with metadata to the sync queue and triggers platform-specific sync.
 * This is a common (multiplatform) use case that delegates platform-specific
 * triggering to SyncTriggerCoordinator.
 */
class EnqueueCollectTaskAndTriggerSyncUseCase(
    private val syncRepository: SyncRepository,
    private val syncTriggerCoordinator: SyncTriggerCoordinator
) {
    suspend operator fun invoke(
        taskType: TaskType,
        taskId: String,
        priority: Int = 10,
        maxAttempts: Int = 3,
        metadata: List<CollectTaskMetadata>,
        submittedBy: String,
    ): Result<String> {
        // submittedBy is currently not persisted at repository level; reserved for future persistence.
        return syncRepository.enqueueCollectTask(
            taskType = taskType,
            taskId = taskId,
            priority = priority,
            maxAttempts = maxAttempts,
            metadata = metadata,
            submittedBy = submittedBy
        ).onSuccess {
            // Trigger platform-specific sync after task is added
            syncTriggerCoordinator.triggerSync()
        }
    }
}
