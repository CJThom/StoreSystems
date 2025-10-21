package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderLineItemEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderLineItem

internal fun CollectOrderLineItemEntity.toDomain(): CollectOrderLineItem {
    return CollectOrderLineItem(
        lineNumber = this.lineNumber,
        sku = this.sku,
        productNumber = this.productNumber,
        productDescription = this.productDescription,
        quantity = this.quantity,
        unitPrice = this.unitPrice,
        productImageUrl = this.productImageUrl
    )
}

internal fun List<CollectOrderLineItemEntity>.toDomain(): List<CollectOrderLineItem> {
    return map { it.toDomain() }
}
