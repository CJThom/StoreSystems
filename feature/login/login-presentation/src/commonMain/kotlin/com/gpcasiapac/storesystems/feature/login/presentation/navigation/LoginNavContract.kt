package com.gpcasiapac.storesystems.feature.login.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.feature.login.api.LoginOutcome

/**
 * Feature-scoped navigation contract for the Login host (Outcome-driven).
 */
object LoginNavContract {
    sealed interface Event : ViewEvent {
        data class Outcome(val outcome: LoginOutcome) : Event
        data class PopBack(val count: Int = 1) : Event
    }
}