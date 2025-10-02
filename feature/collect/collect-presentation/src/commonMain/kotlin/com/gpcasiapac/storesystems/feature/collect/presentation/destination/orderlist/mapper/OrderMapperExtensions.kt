package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper

import com.gpcasiapac.storesystems.common.kotlin.util.StringUtils
import com.gpcasiapac.storesystems.feature.collect.domain.model.Customer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrder
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderState

internal fun CollectOrder.toState(): CollectOrderState {
    return CollectOrderState(
        id = this.id,
        invoiceNumber = this.invoiceNumber,
        webOrderNumber = this.webOrderNumber,
        customerType = this.customer.customerType,
        customerName = getCustomerName(customer = this.customer),
        pickedAt = this.pickedAt
    )
}

private fun getCustomerName(customer: Customer): String {
    return when (customer.customerType) {
        CustomerType.B2B -> customer.accountName ?: "-"
        CustomerType.B2C -> StringUtils.fullName(
            firstName = customer.firstName,
            lastName = customer.lastName
        ).takeIf { it.isNotEmpty() } ?: "-"
    }
}

internal fun List<CollectOrder>.toState(): List<CollectOrderState> {
    return map { it.toState() }
}

