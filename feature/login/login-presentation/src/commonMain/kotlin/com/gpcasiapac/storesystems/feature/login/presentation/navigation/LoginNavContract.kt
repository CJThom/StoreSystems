package com.gpcasiapac.storesystems.feature.login.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent

object LoginNavContract {
    sealed interface Event : ViewEvent {
        data class ToMfa(val userId: String) : Event
        data object ToComplete : Event
        data class PopBack(val count: Int = 1) : Event
    }
}