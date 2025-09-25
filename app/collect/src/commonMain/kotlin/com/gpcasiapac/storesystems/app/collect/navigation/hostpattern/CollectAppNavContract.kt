package com.gpcasiapac.storesystems.app.collect.navigation.hostpattern

import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.feature.collect.api.CollectExternalOutcome
import com.gpcasiapac.storesystems.feature.login.api.LoginExternalOutcome

object CollectAppNavContract {
    sealed interface Event : ViewEvent {
        data class FromLogin(val outcome: LoginExternalOutcome) : Event
        data class FromCollect(val outcome: CollectExternalOutcome) : Event
        data class PopBack(val count: Int = 1) : Event
    }
}