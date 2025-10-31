package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState

internal fun CollectOrderWithCustomer.toListItemState(): CollectOrderListItemState {
    return CollectOrderListItemState(
        invoiceNumber = this.order.invoiceNumber,
        webOrderNumber = this.order.webOrderNumber,
        customerType = this.customer.customerType,
        customerName = this.customer.name,
        pickedAt = this.order.invoiceDateTime
    )
}

internal fun List<CollectOrderWithCustomer>.toListItemState(): List<CollectOrderListItemState> {
    return map { it.toListItemState() }
}
