package com.gpcasiapac.storesystems.feature.collect.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent

/**
 * Legacy, feature-scoped navigation contract for the Collect host.
 * Aligned to outcome-style naming to match global navigation patterns.
 */
object CollectNavContract {
    sealed interface Event : ViewEvent {
        // Outcome-style naming (no destination keywords)
        data class OrderSelected(val orderId: String) : Event
        data class PopBack(val count: Int = 1) : Event
    }
}
