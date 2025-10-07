package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper

import com.gpcasiapac.storesystems.common.kotlin.util.StringUtils
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrder
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderLineItem
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail.model.CollectOrderCustomerState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail.model.CollectOrderLineItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail.model.CollectOrderState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail.model.CollectOrderWithCustomerWithLineItemsState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState


internal fun CollectOrderCustomer.toState(): CollectOrderCustomerState {
    return CollectOrderCustomerState(
        type = this.customerType,
        name = getCustomerName(this),
        customerNumber = this.customerNumber,
        mobileNumber = this.phone
    )
}

internal fun List<CollectOrderCustomer>.toState(): List<CollectOrderCustomerState> {
    return map { it.toState() }
}

private fun getCustomerName(collectOrderCustomer: CollectOrderCustomer): String {
    return when (collectOrderCustomer.customerType) {
        CustomerType.B2B -> collectOrderCustomer.accountName ?: "-"
        CustomerType.B2C -> StringUtils.fullName(
            firstName = collectOrderCustomer.firstName,
            lastName = collectOrderCustomer.lastName
        ).takeIf { it.isNotEmpty() } ?: "-"
    }
}
