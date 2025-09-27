package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.entity.OrderEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order

fun Order.toEntity(): OrderEntity {
    return OrderEntity(
        id = this.id,
        customerType = this.customerType,
        customerName = this.customerName,
        invoiceNumber = this.invoiceNumber,
        webOrderNumber = this.webOrderNumber,
        pickedAt = this.pickedAt,
    )
}

fun List<Order>.toEntity(): List<OrderEntity> {
    return this.map { it.toEntity() }
}
