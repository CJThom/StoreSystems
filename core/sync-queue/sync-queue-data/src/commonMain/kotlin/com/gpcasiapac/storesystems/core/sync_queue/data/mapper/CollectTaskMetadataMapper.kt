package com.gpcasiapac.storesystems.core.sync_queue.data.mapper

import com.gpcasiapac.storesystems.core.sync_queue.api.model.CollectTaskMetadata
import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.entity.CollectTaskMetadataEntity

/**
 * Map CollectTaskMetadataEntity to API model.
 */
fun CollectTaskMetadataEntity.toDomain(): CollectTaskMetadata {
    return CollectTaskMetadata(
        id = id,
        syncTaskId = syncTaskId,
        invoiceNumber = invoiceNumber,
        orderNumber = orderNumber,
        webOrderNumber = webOrderNumber,
        createdDateTime = createdDateTime,
        invoiceDateTime = invoiceDateTime,
        customerNumber = customerNumber,
        customerType = customerType,
        name = name,
        phone = phone
    )
}

/**
 * Map CollectTaskMetadata API model to entity.
 */
fun CollectTaskMetadata.toEntity(): CollectTaskMetadataEntity {
    return CollectTaskMetadataEntity(
        id = id,
        syncTaskId = syncTaskId,
        invoiceNumber = invoiceNumber,
        orderNumber = orderNumber,
        webOrderNumber = webOrderNumber,
        createdDateTime = createdDateTime,
        invoiceDateTime = invoiceDateTime,
        customerNumber = customerNumber,
        customerType = customerType,
        name = name,
        phone = phone
    )
}
