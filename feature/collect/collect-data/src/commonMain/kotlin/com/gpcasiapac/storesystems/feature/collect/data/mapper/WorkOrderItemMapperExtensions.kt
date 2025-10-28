package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderItemEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectWorkOrderItem

fun CollectWorkOrderItem.toEntity(): CollectWorkOrderItemEntity {
    return CollectWorkOrderItemEntity(
        workOrderId = workOrderId,
        invoiceNumber = invoiceNumber,
        position = position,
    )
}
