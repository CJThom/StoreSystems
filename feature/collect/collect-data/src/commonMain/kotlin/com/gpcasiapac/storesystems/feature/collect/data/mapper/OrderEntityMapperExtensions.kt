package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrder

fun CollectOrderEntity.toDomain(): CollectOrder {
    return CollectOrder(
        invoiceNumber = this.invoiceNumber,
        id = this.id,
        orderChannel = this.orderChannel,
        webOrderNumber = this.webOrderNumber,
        orderNumber = this.orderNumber,
        invoiceDateTime = this.invoiceDateTime,
        createdDateTime = this.createdDateTime,
        isLocked = this.isLocked,
        lockedBy = this.lockedBy,
        lockedDateTime = this.lockedDateTime
    )
}

fun List<CollectOrderEntity>.toDomain(): List<CollectOrder> {
    return this.map { it.toDomain() }
}
