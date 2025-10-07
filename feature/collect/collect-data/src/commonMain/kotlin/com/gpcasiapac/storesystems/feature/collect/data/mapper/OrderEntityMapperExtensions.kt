package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrder

fun CollectOrderEntity.toDomain(): CollectOrder {
    return CollectOrder(
        invoiceNumber = this.invoiceNumber,
        salesOrderNumber = this.salesOrderNumber,
        webOrderNumber = this.webOrderNumber,
        createdAt = this.pickedAt,
        pickedAt = this.pickedAt
    )
}

fun List<CollectOrderEntity>.toDomain(): List<CollectOrder> {
    return this.map { it.toDomain() }
}
