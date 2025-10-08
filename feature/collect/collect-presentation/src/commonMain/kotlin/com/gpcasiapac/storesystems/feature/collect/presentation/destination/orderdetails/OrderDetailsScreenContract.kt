package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetails

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model.CollectOrderWithCustomerWithLineItemsState

object OrderDetailsScreenContract {

    @Immutable
    data class State(
        val order: CollectOrderWithCustomerWithLineItemsState?,
        val isLoading: Boolean,
        val error: String?,
    ) : ViewState

    sealed interface Event : ViewEvent {
        data class LoadOrder(val invoiceNumber: String) : Event
        data object Back : Event
    }

    sealed interface Effect : ViewSideEffect {
        data class ShowError(val error: String) : Effect
        sealed interface Outcome : Effect {
            data object Back : Outcome
        }
    }
}