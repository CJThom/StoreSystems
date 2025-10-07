package com.gpcasiapac.storesystems.app.collect.navigation.hostpattern

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.navigation.ViewStateWithNavigation
import com.gpcasiapac.storesystems.feature.collect.api.CollectExternalOutcome
import com.gpcasiapac.storesystems.feature.login.api.LoginExternalOutcome

object CollectAppNavContract {

    data class State(
        override val stack: List<NavKey>
    ) : ViewStateWithNavigation<State> {
        override fun copyWithStack(stack: List<NavKey>): State {
            return copy(stack = stack)
        }
    }

    sealed interface Event : ViewEvent {
        data class FromLogin(val outcome: LoginExternalOutcome) : Event
        data class FromCollect(val outcome: CollectExternalOutcome) : Event
        data class PopBack(val count: Int = 1) : Event
    }
}