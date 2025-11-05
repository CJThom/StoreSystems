package com.gpcasiapac.storesystems.core.sync_queue.api.model

/**
 * API model combining SyncTask with CollectTaskMetadata entries.
 * Exposed by SyncQueueService for tasks that have collect metadata.
 */
data class SyncTaskWithCollectMetadata(
    val task: SyncTask,
    val collectMetadata: List<CollectTaskMetadata>
)
