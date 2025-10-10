package com.gpcasiapac.storesystems.feature.login.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.navigation.ViewStateWithNavigation
import com.gpcasiapac.storesystems.feature.login.api.LoginExternalOutcome
import com.gpcasiapac.storesystems.feature.login.api.LoginOutcome

/**
 * Feature-scoped navigation contract for the Login host (Outcome-driven).
 */
object LoginNavigationContract {
    sealed interface Event : ViewEvent {
        data class Outcome(val outcome: LoginOutcome) : Event
        data object PopBack : Event
    }

    sealed interface Effect : ViewSideEffect {
        data class ExternalOutcome(val outcome: LoginExternalOutcome) : Effect
    }

    /**
     * Login feature navigation state.
     * Currently only contains the navigation stack but can be extended with login-specific state later.
     */
    data class State(override val stack: List<NavKey>) : ViewStateWithNavigation<State> {
        override fun copyWithStack(stack: List<NavKey>): State {
            return copy(stack = stack)
        }
    }
}