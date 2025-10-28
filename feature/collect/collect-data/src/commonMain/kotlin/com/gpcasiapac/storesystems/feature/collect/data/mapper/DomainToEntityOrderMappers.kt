package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderCustomerEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderLineItemEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrder
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderLineItem
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems

internal fun CollectOrder.toEntity(): CollectOrderEntity =
    CollectOrderEntity(
        invoiceNumber = invoiceNumber,
        salesOrderNumber = salesOrderNumber,
        webOrderNumber = webOrderNumber,
        createdAt = createdAt,
        pickedAt = pickedAt,
    )

internal fun CollectOrderCustomer.toEntity(invoiceNumber: String): CollectOrderCustomerEntity =
    CollectOrderCustomerEntity(
        invoiceNumber = invoiceNumber,
        customerNumber = customerNumber,
        customerType = customerType,
        accountName = accountName,
        firstName = firstName,
        lastName = lastName,
        phone = phone,
    )

internal fun CollectOrderLineItem.toEntity(invoiceNumber: String): CollectOrderLineItemEntity =
    CollectOrderLineItemEntity(
        invoiceNumber = invoiceNumber,
        lineNumber = lineNumber,
        sku = sku,
        productNumber = productNumber,
        productDescription = productDescription,
        quantity = quantity,
        unitPrice = unitPrice,
        productImageUrl = productImageUrl,
    )

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
