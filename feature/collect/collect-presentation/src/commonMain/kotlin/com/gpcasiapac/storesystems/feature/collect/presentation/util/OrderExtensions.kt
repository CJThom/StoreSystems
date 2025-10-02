package com.gpcasiapac.storesystems.feature.collect.presentation.util

import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order

val Order.displayName: String
    get() = when {
        // Prefer account name for B2B/account orders
        (customer.customerType == CustomerType.B2B) && !customer.accountName.isNullOrBlank() -> customer.accountName!!
        // Otherwise prefer the person's full name
        customer.fullName.isNotBlank() -> customer.fullName
        invoiceNumber.isNotBlank() -> invoiceNumber
        !webOrderNumber.isNullOrBlank() -> webOrderNumber!!
        else -> id
    }
