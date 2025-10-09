package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderCustomerEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderLineItemEntity
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
            pickedAt = Instant.fromEpochMilliseconds(this.pickedAtEpochMillis),
            signature = null
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
        lineItemEntityList = this.lineItemDtoList.map {
            CollectOrderLineItemEntity(
                invoiceNumber = this.invoiceNumber,
                lineNumber = it.lineNumber,
                sku = it.sku,
                productNumber = it.productNumber,
                productDescription = it.productDescription,
                quantity = it.quantity,
                unitPrice = it.unitPrice
            )
        }
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
