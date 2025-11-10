package com.gpcasiapac.storesystems.feature.collect.domain.util

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.SignButtonGating
import com.gpcasiapac.storesystems.feature.collect.domain.model.SignButtonInputs

/**
 * Pure calculator for Sign button gating.
 * Uses early returns for clarity and future extensibility.
 */
fun deriveSignButtonGating(input: SignButtonInputs): SignButtonGating {
    // 1) Must have at least one order
    if (input.ordersCount <= 0) {
        return SignButtonGating(
            isEnabled = false,
            reasons = listOf(SignButtonGating.Reason("no_orders"))
        )
    }

    // 2) Must have a collecting type selected
    val type = input.collectingType
    if (type == null) {
        return SignButtonGating(
            isEnabled = false,
            reasons = listOf(SignButtonGating.Reason("no_collection_type"))
        )
    }

    // 3) If courier, courier name is required
    if (type == CollectingType.COURIER) {
        val missingCourier = input.courierName.isNullOrBlank()
        if (missingCourier) {
            return SignButtonGating(
                isEnabled = false,
                reasons = listOf(SignButtonGating.Reason("courier_name_missing"))
            )
        }
    }

    // 4) Must have an ID verification selection
    if (!input.isIdVerificationSelected) {
        return SignButtonGating(
            isEnabled = false,
            reasons = listOf(SignButtonGating.Reason("no_id_verification"))
        )
    }

    // Final success
    return SignButtonGating(isEnabled = true)
}
