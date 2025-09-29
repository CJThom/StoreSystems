package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CustomerEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.OrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.network.dto.OrderDto
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import kotlin.time.Instant

fun OrderDto.toEntity(): OrderEntity {

    val customerType = customerTypeFrom(this.customerType)

    val account = if (customerType == CustomerType.B2B) {
        this.accountName?.takeIf { it.isNotBlank() }
            ?: this.customerName?.takeIf { it.isNotBlank() } ?: ""
    } else {
        ""
    }

    return OrderEntity(
        id = this.id,
        invoiceNumber = this.invoiceNumber,
        webOrderNumber = this.webOrderNumber,
        pickedAt = Instant.fromEpochMilliseconds(this.pickedAtEpochMillis),
        customerEntity = CustomerEntity(
            customerNumber = this.customerNumber,
            customerType = customerType,
            accountName = account,
            firstName = this.customerFirstName,
            lastName = this.customerLastName,
            phone = this.customerPhone,
        ),
    )

}

fun List<OrderDto>.toEntity(): List<OrderEntity> {
    return this.map { it.toEntity() }
}

private fun customerTypeFrom(customerType: String): CustomerType {
    return when (customerType.trim().uppercase()) {
        "B2B" -> CustomerType.B2B
        "B2C" -> CustomerType.B2C
        else -> error("Unknown customerType: $customerType")
    }
}