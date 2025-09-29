package com.gpcasiapac.storesystems.feature.history.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.feature.history.api.HistoryExternalOutcome
import com.gpcasiapac.storesystems.feature.history.api.HistoryOutcome

object HistoryNavigationContract {
    sealed interface Event : ViewEvent {
        data class Outcome(val outcome: HistoryOutcome) : Event
        data class PopBack(val count: Int = 1) : Event
    }

    sealed interface Effect : ViewSideEffect {
        data class ExternalOutcome(val externalOutcome: HistoryExternalOutcome) : Effect
    }
}
