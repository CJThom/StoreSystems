package com.gpcasiapac.storesystems.feature.collect.domain.model

import kotlin.time.Instant

data class WorkOrderSummary(
    val id: String,
    val status: String,
    val createdAt: Instant,
    val orderCount: Int
)