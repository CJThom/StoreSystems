package com.gpcasiapac.storesystems.feature.login.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.feature.login.api.LoginExternalOutcome
import com.gpcasiapac.storesystems.feature.login.api.LoginOutcome

/**
 * Feature-scoped navigation contract for the Login host (Outcome-driven).
 */
object LoginNavigationContract {
    sealed interface Event : ViewEvent {
        data class Outcome(val outcome: LoginOutcome) : Event
        data class PopBack(val count: Int = 1) : Event
    }

    sealed interface Effect : ViewSideEffect {
        data class ExternalOutcome(val outcome: LoginExternalOutcome) : Effect
    }
}