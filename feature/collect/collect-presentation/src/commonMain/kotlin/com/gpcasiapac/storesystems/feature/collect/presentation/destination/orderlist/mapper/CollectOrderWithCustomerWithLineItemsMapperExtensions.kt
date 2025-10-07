package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
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
