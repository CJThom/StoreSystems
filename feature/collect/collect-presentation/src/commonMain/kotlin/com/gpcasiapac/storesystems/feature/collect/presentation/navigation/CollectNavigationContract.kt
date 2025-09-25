package com.gpcasiapac.storesystems.feature.collect.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome
import com.gpcasiapac.storesystems.feature.collect.api.CollectExternalOutcome

/**
 * Feature-scoped navigation contract for the Collect host (Outcome-driven).
 */
object CollectNavigationContract {
    sealed interface Event : ViewEvent {
        data class Outcome(val outcome: CollectOutcome) : Event
        data class PopBack(val count: Int = 1) : Event
    }

    sealed interface Effect : ViewSideEffect {
        data class ExternalOutcome(val externalOutcome: CollectExternalOutcome) : Effect
    }
}
