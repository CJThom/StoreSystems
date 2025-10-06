package com.gpcasiapac.storesystems.feature.collect.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.navigation.ViewStateWithNavigation
import com.gpcasiapac.storesystems.feature.collect.api.CollectExternalOutcome
import com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome

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

    /**
     * Collect feature navigation state.
     * Currently only contains the navigation stack but can be extended with collect-specific state later.
     */
    data class State(override val stack: List<NavKey>) : ViewStateWithNavigation<State> {
        override fun copyWithStack(stack: List<NavKey>): State {
            return copy(stack = stack)
        }
    }
}
