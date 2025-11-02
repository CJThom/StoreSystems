package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderCustomerEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderLineItemEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrder
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderLineItem
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber

internal fun CollectOrder.toEntity(): CollectOrderEntity {
    return CollectOrderEntity(
        id = this.id,
        invoiceNumber = this.invoiceNumber,
        orderNumber = this.orderNumber,
        webOrderNumber = this.webOrderNumber,
        orderChannel = this.orderChannel,
        invoiceDateTime = this.invoiceDateTime,
        createdDateTime = this.createdDateTime,
        isLocked = this.isLocked,
        lockedBy = this.lockedBy,
        lockedDateTime = this.lockedDateTime
    )
}

internal fun CollectOrderCustomer.toEntity(invoiceNumber: InvoiceNumber): CollectOrderCustomerEntity {
    return CollectOrderCustomerEntity(
        invoiceNumber = invoiceNumber,
        number = this.number,
        name = this.name,
        phone = this.phone,
        customerType = this.customerType
    )
}

internal fun CollectOrderLineItem.toEntity(invoiceNumber: InvoiceNumber): CollectOrderLineItemEntity {
    return CollectOrderLineItemEntity(
        invoiceNumber = invoiceNumber,
        lineNumber = this.lineNumber,
        sku = this.sku,
        barcode = this.barcode,
        description = this.description,
        quantity = this.quantity,
        imageUrl = this.imageUrl,
    )
}

// TODO: Wtf
internal fun List<CollectOrderWithCustomerWithLineItems>.toEntityTriples(): Triple<List<CollectOrderEntity>, List<CollectOrderCustomerEntity>, List<CollectOrderLineItemEntity>> {
    val orders = ArrayList<CollectOrderEntity>(size)
    val customers = ArrayList<CollectOrderCustomerEntity>(size)
    val lineItems = ArrayList<CollectOrderLineItemEntity>(size * 3) // rough guess
    for (rel in this) {
        val order = rel.order
        orders += order.toEntity()
        customers += rel.customer.toEntity(order.invoiceNumber)
        rel.lineItemList.forEach { li ->
            lineItems += li.toEntity(order.invoiceNumber)
        }
    }
    return Triple(orders, customers, lineItems)
}
