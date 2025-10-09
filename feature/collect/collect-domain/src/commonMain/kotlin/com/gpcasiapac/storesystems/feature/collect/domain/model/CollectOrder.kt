package com.gpcasiapac.storesystems.feature.collect.domain.model

import kotlin.time.Instant

data class CollectOrder(
    val invoiceNumber: String,
    val salesOrderNumber: String,
    val webOrderNumber: String?,
    val createdAt: Instant,
    val pickedAt: Instant,
    val signature: String?
)