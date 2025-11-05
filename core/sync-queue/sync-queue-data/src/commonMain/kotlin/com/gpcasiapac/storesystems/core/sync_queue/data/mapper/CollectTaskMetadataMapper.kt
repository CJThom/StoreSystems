package com.gpcasiapac.storesystems.core.sync_queue.data.mapper

import com.gpcasiapac.storesystems.core.sync_queue.api.model.CollectTaskMetadata
import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.entity.CollectTaskMetadataEntity

/**
 * Map CollectTaskMetadataEntity to API model.
 */
fun CollectTaskMetadataEntity.toApiModel(): CollectTaskMetadata {
    return CollectTaskMetadata(
        id = id,
        syncTaskId = syncTaskId,
        invoiceNumber = invoiceNumber,
        salesOrderNumber = salesOrderNumber,
        webOrderNumber = webOrderNumber,
        orderCreatedAt = orderCreatedAt,
        orderPickedAt = orderPickedAt,
        customerNumber = customerNumber,
        customerType = customerType,
        accountName = accountName,
        firstName = firstName,
        lastName = lastName,
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
        salesOrderNumber = salesOrderNumber,
        webOrderNumber = webOrderNumber,
        orderCreatedAt = orderCreatedAt,
        orderPickedAt = orderPickedAt,
        customerNumber = customerNumber,
        customerType = customerType,
        accountName = accountName,
        firstName = firstName,
        lastName = lastName,
        phone = phone
    )
}
