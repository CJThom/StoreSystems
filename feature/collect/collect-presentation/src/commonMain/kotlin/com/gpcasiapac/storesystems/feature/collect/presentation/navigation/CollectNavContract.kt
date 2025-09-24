package com.gpcasiapac.storesystems.feature.collect.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome

/**
 * Feature-scoped navigation contract for the Collect host (Outcome-driven).
 */
object CollectNavContract {
    sealed interface Event : ViewEvent {
        data class Outcome(val outcome: CollectOutcome) : Event
        data class PopBack(val count: Int = 1) : Event
    }
}
