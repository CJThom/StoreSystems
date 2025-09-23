package com.gpcasiapac.storesystems.feature.login.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent

object LoginNavContract {
    sealed interface Event : ViewEvent {
        data class SubmitCredentials(val userId: String) : Event
        data object SubmitOtpSuccess : Event
        data class Pop(val count: Int = 1) : Event
    }
}