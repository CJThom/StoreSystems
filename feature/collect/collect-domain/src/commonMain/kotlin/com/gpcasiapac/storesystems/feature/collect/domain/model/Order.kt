package com.gpcasiapac.storesystems.feature.collect.domain.model

import kotlin.time.Instant

data class Order(
    val id: String,
    val customerType: CustomerType,
    // For B2B/account orders only
    val accountName: String?,
    val invoiceNumber: String,
    val webOrderNumber: String?,
    val pickedAt: Instant,
    // Grouped customer details for B2C (and generally person-level info)
    val customer: Customer,
)