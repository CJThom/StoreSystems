package com.gpcasiapac.storesystems.feature.login.presentation.login_screen

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState

object LoginScreenContract {

    @Immutable
    data class State(
        val username: String,
        val password: String,
        val isLoading: Boolean,
        val isLoginEnabled: Boolean,
        val error: String?
    ) : ViewState
    
    sealed interface Event : ViewEvent {
        data class UpdateUsername(val username: String) : Event
        data class UpdatePassword(val password: String) : Event
        data object SubmitCredentials : Event
        data object ClearError : Event
    }
    
    sealed interface Effect : ViewSideEffect {
        data class ShowToast(val message: String) : Effect
        data class ShowError(val error: String) : Effect
        
        // Outcomes emitted to be handled by the feature host/navigation layer
        sealed interface Outcome : Effect {
            data object LoginCompleted : Outcome
            data class MfaRequired(val userId: String) : Outcome
        }
    }
}
