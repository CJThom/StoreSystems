package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.WorkOrderWithOrdersRelation
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectWorkOrder
import com.gpcasiapac.storesystems.feature.collect.domain.model.WorkOrderWithOrderWithCustomers

fun CollectWorkOrderEntity.toDomain(): CollectWorkOrder =
    CollectWorkOrder(
        workOrderId = workOrderId,
        userId = userId,
        createdAt = createdAt,
        signature = signature,
        signedAt = signedAt,
        signedByName = signedByName,
        collectingType = collectingType,
        courierName = courierName,
    )

