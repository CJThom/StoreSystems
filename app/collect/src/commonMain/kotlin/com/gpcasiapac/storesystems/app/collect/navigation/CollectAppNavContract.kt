package com.gpcasiapac.storesystems.app.collect.navigation

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.navigation.ViewStateWithNavigation
import com.gpcasiapac.storesystems.feature.collect.api.CollectExternalOutcome
import com.gpcasiapac.storesystems.feature.history.api.HistoryExternalOutcome
import com.gpcasiapac.storesystems.feature.login.api.LoginExternalOutcome
import kotlinx.serialization.Serializable

object CollectAppNavContract {
    
    @Serializable
    sealed interface Destination : NavKey {
        
        @Serializable
        data object LoginHost : Destination

        @Serializable
        data object CollectHost : Destination
        
        @Serializable
        data object HistoryHost : Destination
    }

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
        data class FromHistory(val outcome: HistoryExternalOutcome) : Event
        data object PopBack : Event
    }
}