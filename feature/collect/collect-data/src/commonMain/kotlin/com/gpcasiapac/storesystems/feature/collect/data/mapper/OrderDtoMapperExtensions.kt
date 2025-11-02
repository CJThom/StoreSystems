package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.data.network.dto.CollectOrderDto
import com.gpcasiapac.storesystems.feature.collect.data.time.toKotlinInstantOrEpoch0
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrder
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderLineItem
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderChannel


// DTO -> Domain mappers for new structure
fun CollectOrderDto.toDomain(): CollectOrderWithCustomerWithLineItems {

    val invoiceNumber = InvoiceNumber(this.invoiceNumber)

    val order = CollectOrder(
        id = this.id,
        invoiceNumber = invoiceNumber,
        orderNumber = this.orderNumber,
        webOrderNumber = this.webOrderNumber,
        orderChannel = if (this.orderChannel == 1) OrderChannel.B2B else OrderChannel.B2C,
        invoiceDateTime = this.invoiceDateTime.toKotlinInstantOrEpoch0(),
        createdDateTime = this.createdDateTime.toKotlinInstantOrEpoch0(),
        isLocked = this.isLocked,
        lockedBy = this.lockedBy,
        lockedDateTime = this.lockedDateTime.toKotlinInstantOrEpoch0()
    )

    val customer = CollectOrderCustomer(
        invoiceNumber = invoiceNumber,
        number = this.customer.number,
        name = this.customer.name,
        phone = this.customer.phone,
        customerType = if (this.orderChannel == 1) CustomerType.B2B else CustomerType.B2C,
    )

    val lineItemList: List<CollectOrderLineItem> = this.lineItems.map { lineItemDto ->
        CollectOrderLineItem(
            invoiceNumber = invoiceNumber,
            lineNumber = lineItemDto.lineNumber.toIntOrNull() ?: 0,
            sku = lineItemDto.sku,
            barcode = lineItemDto.barcode,
            description = lineItemDto.description,
            quantity = lineItemDto.quantity,
            imageUrl = lineItemDto.imageUrl,
        )
    }

    return CollectOrderWithCustomerWithLineItems(
        order = order,
        customer = customer,
        lineItemList = lineItemList,
    )

}

fun List<CollectOrderDto>.toDomain(): List<CollectOrderWithCustomerWithLineItems> {
    return this.map { it.toDomain() }
}
