package com.gpcasiapac.storesystems.feature.collect.domain.model

import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber


data class CollectOrderCustomer(
    val invoiceNumber: InvoiceNumber,
    val number: String,
    val name: String,
    val phone: String?,
    val customerType: CustomerType
)
