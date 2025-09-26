package com.gpcasiapac.storesystems.feature.collect.presentation.util

import com.gpcasiapac.storesystems.feature.collect.domain.model.Order

val Order.displayName: String
    get() = when {
        customerName.isNotBlank() -> customerName
        invoiceNumber.isNotBlank() -> invoiceNumber
        !webOrderNumber.isNullOrBlank() -> webOrderNumber!!
        else -> id
    }
