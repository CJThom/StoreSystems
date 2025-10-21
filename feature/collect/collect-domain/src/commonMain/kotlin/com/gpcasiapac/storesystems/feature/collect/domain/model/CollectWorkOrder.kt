package com.gpcasiapac.storesystems.feature.collect.domain.model

import kotlin.time.Instant


data class CollectWorkOrder(
    val workOrderId: String,
    val userId: String,
    val createdAt: Instant,
    val signature: String?,
    val signedAt: Instant?,
    val signedByName: String?,
    val collectingType: CollectingType,
    val courierName: String,
)