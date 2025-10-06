package com.gpcasiapac.storesystems.feature.collect.domain.model

import kotlin.time.Instant

data class CollectOrder(
    val id: String,
    val invoiceNumber: String,
    val webOrderNumber: String?,
    val pickedAt: Instant,
    val customer: Customer,
)