package com.gpcasiapac.storesystems.feature.collect.domain.model


data class CollectOrderCustomer(
    val invoiceNumber: String,
    val number: String,
    val name: String,
    val phone: String?,
    val customerType: CustomerType
)
