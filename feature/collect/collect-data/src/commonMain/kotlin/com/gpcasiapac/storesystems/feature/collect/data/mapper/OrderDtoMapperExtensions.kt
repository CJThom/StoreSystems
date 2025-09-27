package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.OrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.network.dto.OrderDto
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import kotlin.time.Instant

fun OrderDto.toEntity(): OrderEntity {
    return OrderEntity(
        id = this.id,
        customerType = customerTypeFrom(this.customerType),
        customerName = this.customerName,
        invoiceNumber = this.invoiceNumber,
        webOrderNumber = this.webOrderNumber,
        pickedAt = Instant.fromEpochMilliseconds(this.pickedAtEpochMillis),
    )
}

fun List<OrderDto>.toEntity(): List<OrderEntity> {
    return this.map { it.toEntity() }
}

private fun customerTypeFrom(customerType: String) = when (customerType.trim().uppercase()) {
    "B2B" -> CustomerType.B2B
    "B2C" -> CustomerType.B2C
    else -> error("Unknown customerType: $customerType")
}
