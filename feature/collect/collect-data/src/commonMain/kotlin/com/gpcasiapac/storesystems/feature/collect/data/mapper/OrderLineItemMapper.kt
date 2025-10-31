package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderLineItemEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderLineItem

internal fun CollectOrderLineItemEntity.toDomain(): CollectOrderLineItem {
    return CollectOrderLineItem(
        lineNumber = this.lineNumber,
        invoiceNumber = this.invoiceNumber,
        sku = this.sku,
        barcode = this.barcode,
        description = this.description,
        quantity = this.quantity,
        imageUrl = this.imageUrl,
    )
}

internal fun List<CollectOrderLineItemEntity>.toDomain(): List<CollectOrderLineItem> {
    return map { it.toDomain() }
}