package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.feature.collect.presentation.model.Order

object OrderDetailScreenContract {

    @Immutable
    data class State(
        val orderId: String?,
        val order: Order?,
        val isLoading: Boolean,
        val error: String?,
    ) : ViewState

    sealed interface Event : ViewEvent {
        data class LoadOrder(val orderId: String) : Event
        data object Refresh : Event
        data object ClearError : Event
        data object Back : Event
    }

    sealed interface Effect : ViewSideEffect {
        data class ShowToast(val message: String) : Effect
        data class ShowError(val error: String) : Effect

        sealed interface Outcome : Effect {
            data object Back : Outcome
        }
    }
}
