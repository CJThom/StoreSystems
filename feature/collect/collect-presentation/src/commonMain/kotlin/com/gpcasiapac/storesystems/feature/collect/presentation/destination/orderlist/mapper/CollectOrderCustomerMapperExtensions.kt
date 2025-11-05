package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderCustomer
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model.CollectOrderCustomerState


internal fun CollectOrderCustomer.toState(): CollectOrderCustomerState {
    return CollectOrderCustomerState(
        type = this.customerType,
        name = this.name,
        customerNumber = this.number,
        mobileNumber = this.phone
    )
}

internal fun List<CollectOrderCustomer>.toState(): List<CollectOrderCustomerState> {
    return map { it.toState() }
}
