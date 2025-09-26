package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.feature.collect.presentation.model.Order

object OrderListScreenContract {

    @Immutable
    data class State(
        val orderList: List<Order>,
        val isMultiSelectionEnabled: Boolean,
        val selectedOrderIdList: Set<String>,
        val orderCount: Int,
        val isSelectAllChecked: Boolean,
        val searchText: String,
        val searchHintResultList: List<Order>,
        val filteredOrderList: List<Order>,
        val isLoading: Boolean,
        val isRefreshing: Boolean,
        val submittedOrder: Order?,
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

        sealed interface Outcome : Effect {
            data class OrderSelected(val orderId: String) : Outcome
        }
    }
}
