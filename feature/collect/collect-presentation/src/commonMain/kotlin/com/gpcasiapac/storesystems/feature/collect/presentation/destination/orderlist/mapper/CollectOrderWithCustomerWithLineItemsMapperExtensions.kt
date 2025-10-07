package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail.model.CollectOrderWithCustomerWithLineItemsState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState

internal fun CollectOrderWithCustomerWithLineItems.toState(): CollectOrderWithCustomerWithLineItemsState {
    return CollectOrderWithCustomerWithLineItemsState(
        order = this.order.toState(),
        customer = this.customer.toState(),
        lineItemList = this.lineItemList.toState()
    )
}

internal fun List<CollectOrderWithCustomerWithLineItems>.toState(): List<CollectOrderWithCustomerWithLineItemsState> {
    return map { it.toState() }
}


internal fun CollectOrderWithCustomerWithLineItems.toListItemState(): CollectOrderListItemState {
    return CollectOrderListItemState(
        id = this.order.id,
        invoiceNumber = this.order.invoiceNumber,
        webOrderNumber = this.order.webOrderNumber,
        customerType = this.customer.customerType,
        customerName = getCustomerName(collectOrderCustomer = this.customer),
        pickedAt = this.order.pickedAt
    )
}

internal fun List<CollectOrderWithCustomerWithLineItems>.toListItemState(): List<CollectOrderListItemState> {
    return map { it.toListItemState() }
}
