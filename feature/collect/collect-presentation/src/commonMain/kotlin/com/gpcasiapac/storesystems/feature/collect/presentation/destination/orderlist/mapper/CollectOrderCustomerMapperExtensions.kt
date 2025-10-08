package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.util.CustomerNameFormatter
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model.CollectOrderCustomerState


internal fun CollectOrderCustomer.toState(): CollectOrderCustomerState {
    return CollectOrderCustomerState(
        type = this.customerType,
        name = CustomerNameFormatter.getDisplayName(
            customerType = this.customerType,
            accountName = this.accountName,
            firstName = this.firstName,
            lastName = this.lastName
        ),
        customerNumber = this.customerNumber,
        mobileNumber = this.phone
    )
}

internal fun List<CollectOrderCustomer>.toState(): List<CollectOrderCustomerState> {
    return map { it.toState() }
}
