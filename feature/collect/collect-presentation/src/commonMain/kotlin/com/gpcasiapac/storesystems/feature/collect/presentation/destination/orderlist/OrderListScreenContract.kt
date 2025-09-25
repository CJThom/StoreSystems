package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.feature.collect.presentation.model.Order

object OrderListScreenContract {

    @Immutable
    data class State(
        val orders: List<Order>,
        val isLoading: Boolean,
        val error: String?,
    ) : ViewState

    sealed interface Event : ViewEvent {
        data object Load : Event
        data object Refresh : Event
        data class OpenOrder(val orderId: String) : Event
        data object ClearError : Event
    }

    sealed interface Effect : ViewSideEffect {
        data class ShowToast(val message: String) : Effect
        data class ShowError(val error: String) : Effect

        sealed interface Outcome :
            Effect {
            data class OrderSelected(val orderId: String) : Outcome
        }
    }
}
