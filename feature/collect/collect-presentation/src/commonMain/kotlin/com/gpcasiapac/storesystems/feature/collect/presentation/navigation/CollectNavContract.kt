package com.gpcasiapac.storesystems.feature.collect.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureDestination

object CollectNavContract {
    sealed interface Event : ViewEvent, CollectFeatureDestination {
        data class ToOrderDetails(val orderId: String) : Event
        data class PopBack(val count: Int = 1) : Event
    }
}
