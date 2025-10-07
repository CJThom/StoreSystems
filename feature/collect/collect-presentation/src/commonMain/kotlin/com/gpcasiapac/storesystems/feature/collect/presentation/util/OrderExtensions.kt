package com.gpcasiapac.storesystems.feature.collect.presentation.util

import com.gpcasiapac.storesystems.common.kotlin.util.StringUtils
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrder

//val CollectOrder.displayName: String
//    get() {
//        val customerFullName = StringUtils.fullName(collectOrderCustomer.firstName, collectOrderCustomer.lastName)
//        return when {
//            // Prefer account name for B2B/account orders
//            (collectOrderCustomer.customerType == CustomerType.B2B) && !collectOrderCustomer.accountName.isNullOrBlank() -> collectOrderCustomer.accountName!!
//            // Otherwise prefer the person's full name
//            customerFullName.isNotBlank() -> customerFullName
//            invoiceNumber.isNotBlank() -> invoiceNumber
//            !webOrderNumber.isNullOrBlank() -> webOrderNumber!!
//            else -> id
//        }
//    }
