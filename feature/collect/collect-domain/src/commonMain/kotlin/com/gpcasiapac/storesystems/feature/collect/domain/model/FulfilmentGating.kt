package com.gpcasiapac.storesystems.feature.collect.domain.model

import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId

/**
 * Cohesive gating facts for the Order Fulfilment flow.
 * This model intentionally stays close to domain facts and avoids presentation-only flags
 * like "idVerified" which belong to the UI layer.
 */
data class FulfilmentGating(
    val workOrderId: WorkOrderId,
    val hasOrders: Boolean,
    val collectingType: CollectingType?,
    val courierName: String?,
    val hasSignature: Boolean,
) {
    val hasCollectingType: Boolean get() = collectingType != null
}
