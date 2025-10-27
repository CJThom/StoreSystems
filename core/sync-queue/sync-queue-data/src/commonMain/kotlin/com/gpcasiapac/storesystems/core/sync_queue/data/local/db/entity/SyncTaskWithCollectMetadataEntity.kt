package com.gpcasiapac.storesystems.core.sync_queue.data.local.db.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Room relation class for joining SyncTaskEntity with CollectTaskMetadataEntity.
 * Used for efficient JOIN queries.
 */
data class SyncTaskWithCollectMetadataEntity(
    @Embedded
    val task: SyncTaskEntity,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "sync_task_id"
    )
    val metadata: CollectTaskMetadataEntity?
)
