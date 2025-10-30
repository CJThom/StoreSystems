package com.gpcasiapac.storesystems.core.sync_queue.data.mapper

import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTaskWithCollectMetadata
import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.entity.SyncTaskWithCollectMetadataEntity

/**
 * Map entity join result to API model.
 */
fun SyncTaskWithCollectMetadataEntity.toApiModel(): SyncTaskWithCollectMetadata {
    return SyncTaskWithCollectMetadata(
        task = task.toDomain(),
        collectMetadata = metadata.map { it.toApiModel() }
    )
}

/**
 * Map list of entity joins to API models.
 */
fun List<SyncTaskWithCollectMetadataEntity>.toApiModels(): List<SyncTaskWithCollectMetadata> {
    return map { it.toApiModel() }
}
