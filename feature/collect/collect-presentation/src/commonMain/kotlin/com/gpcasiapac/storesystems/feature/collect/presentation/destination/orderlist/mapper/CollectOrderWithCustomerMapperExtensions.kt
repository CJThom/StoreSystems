package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.util.CustomerNameFormatter
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState

//internal fun CollectOrderWithCustomer.toState(): CollectOrderWithCustomerState {
//    return CollectOrderWithCustomerState(
//        order = this.order.toState(),
//        customer = this.customer.toState()
//    )
//}
//
//internal fun List<CollectOrderWithCustomer>.toState(): List<CollectOrderWithCustomerState> {
//    return map { it.toState() }
//}


internal fun CollectOrderWithCustomer.toListItemState(): CollectOrderListItemState {
    return CollectOrderListItemState(
        invoiceNumber = this.order.invoiceNumber,
        webOrderNumber = this.order.webOrderNumber,
        customerType = this.customer.customerType,
        customerName = CustomerNameFormatter.getDisplayName(
            customerType = this.customer.customerType,
            accountName = this.customer.accountName,
            firstName = this.customer.firstName,
            lastName = this.customer.lastName
        ),
        pickedAt = this.order.pickedAt
    )
}

internal fun List<CollectOrderWithCustomer>.toListItemState(): List<CollectOrderListItemState> {
    return map { it.toListItemState() }
}
