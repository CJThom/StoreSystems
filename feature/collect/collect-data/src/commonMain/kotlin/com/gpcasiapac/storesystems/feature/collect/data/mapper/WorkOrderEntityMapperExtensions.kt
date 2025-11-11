package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectWorkOrder

fun CollectWorkOrderEntity.toDomain(): CollectWorkOrder {
    return CollectWorkOrder(
        workOrderId = workOrderId,
        userId = userId,
        createdAt = createdAt,
        collectingType = collectingType,
        courierName = courierName,
        idVerified = idVerified,
    )
}
