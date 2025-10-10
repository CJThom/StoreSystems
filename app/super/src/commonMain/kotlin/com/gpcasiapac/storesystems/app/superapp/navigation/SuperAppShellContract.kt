package com.gpcasiapac.storesystems.app.superapp.navigation

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.navigation.ViewStateWithNavigation
import com.gpcasiapac.storesystems.feature.login.api.LoginExternalOutcome
import kotlinx.serialization.Serializable

@Serializable
sealed interface SuperAppDestination : NavKey {
    @Serializable
    data object LoginHost : SuperAppDestination

    @Serializable
    data object MainHost : SuperAppDestination
}

object SuperAppShellContract {

    data class State(override val stack: List<NavKey>) : ViewStateWithNavigation<State> {
        override fun copyWithStack(stack: List<NavKey>): State{
            return copy(stack = stack)
        }
    }

    sealed interface Event : ViewEvent {
        data class FromLogin(val outcome: LoginExternalOutcome) : Event
        data object PopBack : Event
    }
}
