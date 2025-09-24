package com.gpcasiapac.storesystems.app.collect.navigation

import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent

object CollectAppNavContract {
    sealed interface Event : ViewEvent {
        data object ToCollectHost : Event
        data class PopBack(val count: Int = 1) : Event
    }
}
