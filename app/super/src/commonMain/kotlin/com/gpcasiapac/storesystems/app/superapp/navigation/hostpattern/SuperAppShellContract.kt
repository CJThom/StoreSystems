package com.gpcasiapac.storesystems.app.superapp.navigation.hostpattern

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
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
    sealed interface Event : ViewEvent {
        data class FromLogin(val outcome: LoginExternalOutcome) : Event
        data class PopBack(val count: Int = 1) : Event
    }
}
