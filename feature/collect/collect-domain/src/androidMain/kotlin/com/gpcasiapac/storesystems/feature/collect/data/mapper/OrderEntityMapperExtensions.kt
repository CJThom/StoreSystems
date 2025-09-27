package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.entity.OrderEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import kotlin.time.Instant

fun OrderEntity.toDomain(): Order {
    return Order(
        id = this.id,
        customerType = this.customerType,
        customerName = this.customerName,
        invoiceNumber = this.invoiceNumber,
        webOrderNumber = this.webOrderNumber,
        pickedAt = Instant.fromEpochMilliseconds(this.pickedAtEpochMillis),
    )
}

fun List<OrderEntity>.toDomain(): List<Order> {
    return this.map { it.toDomain() }
}
