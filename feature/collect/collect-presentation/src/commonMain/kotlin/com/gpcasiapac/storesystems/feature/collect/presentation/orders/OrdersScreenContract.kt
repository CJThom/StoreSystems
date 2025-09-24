package com.gpcasiapac.storesystems.feature.collect.presentation.orders

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState

object OrdersScreenContract {

    @Immutable
    data class Order(
        val id: String,
        val title: String,
    )

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

        sealed interface Navigation : Effect {
            data class OrderSelected(val orderId: String) : Navigation
        }
    }
}
