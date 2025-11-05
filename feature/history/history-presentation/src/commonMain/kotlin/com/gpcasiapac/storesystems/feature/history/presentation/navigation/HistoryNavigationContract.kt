package com.gpcasiapac.storesystems.feature.history.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.navigation.ViewStateWithNavigation
import com.gpcasiapac.storesystems.feature.history.api.HistoryExternalOutcome
import com.gpcasiapac.storesystems.feature.history.api.HistoryOutcome

object HistoryNavigationContract {
    sealed interface Event : ViewEvent {
        data class Outcome(val outcome: HistoryOutcome) : Event
        data class PopBack(val count: Int = 1) : Event
        data class Push(val key: com.gpcasiapac.storesystems.feature.history.api.HistoryFeatureDestination) : Event
    }

    sealed interface Effect : ViewSideEffect {
        data class ExternalOutcome(val externalOutcome: HistoryExternalOutcome) : Effect
    }

    /**
     * History feature navigation state.
     * Currently only contains the navigation stack but can be extended with history-specific state later.
     */
    data class State(override val stack: List<NavKey>) : ViewStateWithNavigation<State> {
        override fun copyWithStack(stack: List<NavKey>): State {
            return copy(stack = stack)
        }
    }
}
