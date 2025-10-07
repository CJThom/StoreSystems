package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderLineItemEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderLineItem

internal fun CollectOrderLineItemEntity.toDomain(): CollectOrderLineItem {
    return CollectOrderLineItem(
        invoiceNumber = this.invoiceNumber,
        lineNumber = this.lineNumber
    )
}

internal fun List<CollectOrderLineItemEntity>.toDomain(): List<CollectOrderLineItem> {
    return map { it.toDomain() }
}
