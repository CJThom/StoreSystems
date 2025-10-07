package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.OrderLineItemEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderLineItem

internal fun OrderLineItemEntity.toDomain(): CollectOrderLineItem {
    return CollectOrderLineItem(
        lineNumber = lineNumber
    )
}

internal fun List<OrderLineItemEntity>.toDomain(): List<CollectOrderLineItem> {
    return map { it.toDomain() }
}
