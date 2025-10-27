package com.gpcasiapac.storesystems.feature.collect.domain.model

import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import kotlin.time.Instant


data class CollectWorkOrder(
    val workOrderId: WorkOrderId,
    val userId: String,
    val createdAt: Instant,
    val collectingType: CollectingType?,
    val courierName: String?,
)