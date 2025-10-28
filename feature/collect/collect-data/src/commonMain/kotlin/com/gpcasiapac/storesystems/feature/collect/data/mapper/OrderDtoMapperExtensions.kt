package com.gpcasiapac.storesystems.feature.collect.data.mapper
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderCustomerEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderLineItemEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.CollectOrderWithCustomerWithLineItemsRelation
import com.gpcasiapac.storesystems.feature.collect.data.network.dto.CollectOrderDto
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrder
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderLineItem
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import kotlin.time.Instant

// Existing DTO -> Relation mapper (kept for any callers still using relations)
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
        lineItemEntityList = this.lineItemDtoList.map {
            CollectOrderLineItemEntity(
                invoiceNumber = this.invoiceNumber,
                lineNumber = it.lineNumber,
                sku = it.sku,
                productNumber = it.productNumber,
                productDescription = it.productDescription,
                quantity = it.quantity,
                unitPrice = it.unitPrice,
                productImageUrl = it.productImageUrl
            )
        }
    )
}

fun List<CollectOrderDto>.toRelation(): List<CollectOrderWithCustomerWithLineItemsRelation> {
    return this.map { it.toRelation() }
}

// NEW: DTO -> Domain mappers (avoid going through Relation)
fun CollectOrderDto.toDomain(): CollectOrderWithCustomerWithLineItems {
    val order = CollectOrder(
        invoiceNumber = this.invoiceNumber,
        salesOrderNumber = this.salesOrderNumber,
        webOrderNumber = this.webOrderNumber,
        createdAt = Instant.fromEpochMilliseconds(this.createdAtEpochMillis),
        pickedAt = Instant.fromEpochMilliseconds(this.pickedAtEpochMillis),
    )
    val customer = CollectOrderCustomer(
        customerNumber = this.customerNumber,
        customerType = customerTypeFrom(this.customerType),
        accountName = this.accountName,
        firstName = this.customerFirstName,
        lastName = this.customerLastName,
        phone = this.customerPhone,
    )
    val lineItems: List<CollectOrderLineItem> = this.lineItemDtoList.map { li ->
        CollectOrderLineItem(
            lineNumber = li.lineNumber,
            sku = li.sku,
            productNumber = li.productNumber,
            productDescription = li.productDescription,
            quantity = li.quantity,
            unitPrice = li.unitPrice,
            productImageUrl = li.productImageUrl,
        )
    }
    return CollectOrderWithCustomerWithLineItems(
        order = order,
        customer = customer,
        lineItemList = lineItems,
    )
}

fun List<CollectOrderDto>.toDomain(): List<CollectOrderWithCustomerWithLineItems> = this.map { it.toDomain() }

private fun customerTypeFrom(customerType: String): CustomerType {
    return when (customerType.trim().uppercase()) {
        "B2B" -> CustomerType.B2B
        "B2C" -> CustomerType.B2C
        else -> error("Unknown customerType: $customerType")
    }
}
