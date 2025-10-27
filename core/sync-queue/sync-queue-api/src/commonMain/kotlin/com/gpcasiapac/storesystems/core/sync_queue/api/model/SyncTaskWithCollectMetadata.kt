package com.gpcasiapac.storesystems.core.sync_queue.api.model

/**
 * API model combining SyncTask with optional CollectTaskMetadata.
 * Exposed by SyncQueueService for tasks that have collect metadata.
 */
data class SyncTaskWithCollectMetadata(
    val task: SyncTask,
    val collectMetadata: CollectTaskMetadata?
)
