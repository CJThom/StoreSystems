package com.gpcasiapac.storesystems.feature.collect.domain.model

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType

/**
 * Inputs required to derive whether the Sign action should be enabled.
 */
data class SignButtonInputs(
    val ordersCount: Int,
    val collectingType: CollectingType?,
    val courierName: String?,
    val isIdVerificationSelected: Boolean,
)
