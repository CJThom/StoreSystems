package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderCustomerEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.CollectOrderWithCustomerWithLineItemsRelation
import com.gpcasiapac.storesystems.feature.collect.data.network.dto.CollectOrderDto
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import kotlin.time.Instant

fun CollectOrderDto.toRelation(): CollectOrderWithCustomerWithLineItemsRelation {
    return CollectOrderWithCustomerWithLineItemsRelation(
        orderEntity = CollectOrderEntity(
            invoiceNumber = this.invoiceNumber,
            salesOrderNumber = this.salesOrderNumber,
            webOrderNumber = this.webOrderNumber,
            createdAt = Instant.fromEpochMilliseconds(this.createdAtEpochMillis),
            pickedAt = Instant.fromEpochMilliseconds(this.pickedAtEpochMillis)
        ),
        customerEntity = CollectOrderCustomerEntity(
            invoiceNumber = this.invoiceNumber,
            customerNumber = this.customerNumber,
            customerType = customerTypeFrom(this.customerType),
            accountName = this.accountName,
            firstName = this.customerFirstName,
            lastName = this.customerLastName,
            phone = this.customerPhone
        ),
        lineItemEntityList = emptyList()
    )

}

fun List<CollectOrderDto>.toRelation(): List<CollectOrderWithCustomerWithLineItemsRelation> {
    return this.map { it.toRelation() }
}

private fun customerTypeFrom(customerType: String): CustomerType {
    return when (customerType.trim().uppercase()) {
        "B2B" -> CustomerType.B2B
        "B2C" -> CustomerType.B2C
        else -> error("Unknown customerType: $customerType")
    }
}