package com.gpcasiapac.storesystems.feature.collect.domain.model


data class CollectOrderCustomer(
    val customerNumber: String,
    val customerType: CustomerType,
    val accountName: String?,
    val firstName: String?,
    val lastName: String?,
    val phone: String?
)
