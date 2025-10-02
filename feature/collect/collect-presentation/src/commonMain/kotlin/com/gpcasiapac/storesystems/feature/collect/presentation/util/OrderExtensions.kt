package com.gpcasiapac.storesystems.feature.collect.presentation.util

import com.gpcasiapac.storesystems.common.kotlin.util.StringUtils
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order

val Order.displayName: String
    get() {
        val customerFullName = StringUtils.fullName(customer.firstName, customer.lastName)
        return when {
            // Prefer account name for B2B/account orders
            (customer.customerType == CustomerType.B2B) && !customer.accountName.isNullOrBlank() -> customer.accountName!!
            // Otherwise prefer the person's full name
            customerFullName.isNotBlank() -> customerFullName
            invoiceNumber.isNotBlank() -> invoiceNumber
            !webOrderNumber.isNullOrBlank() -> webOrderNumber!!
            else -> id
        }
    }
