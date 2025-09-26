package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class OrderListScreenViewModel(
    private val observeOrdersBySearch: com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveOrdersBySearchUseCase,
    private val refreshOrders: com.gpcasiapac.storesystems.feature.collect.domain.usecase.RefreshOrdersUseCase,
) : MVIViewModel<OrderListScreenContract.Event, OrderListScreenContract.State, OrderListScreenContract.Effect>() {

    override fun setInitialState(): OrderListScreenContract.State = OrderListScreenContract.State(
        orderList = emptyList(),
        isLoading = true,
        error = null,
    )

    override suspend fun awaitReadiness(): Boolean {
        // Order list screen doesn't require session readiness for this placeholder
        return true
    }

    override fun handleReadinessFailed() {
        // Not applicable for this screen as readiness is always true
    }

    override fun onStart() {
        // Observe orders filtered by search text from state
        val searchTextFlow = viewState
            .map { it.searchText }
            .distinctUntilChanged()

        observeOrdersBySearch(searchTextFlow)
            .onEach { orders ->
                setState { copy(orderList = orders, orderCount = orders.size, isLoading = false, error = null) }
            }
            .launchIn(viewModelScope)

        // Trigger initial refresh to seed data
        viewModelScope.launch { doRefresh(successToast = "Orders loaded") }
    }

    // TABLE OF CONTENTS - All possible events handled here
    override fun handleEvents(event: OrderListScreenContract.Event) {
        when (event) {
            is OrderListScreenContract.Event.Load -> viewModelScope.launch { doRefresh(successToast = "Orders loaded") }
            is OrderListScreenContract.Event.Refresh -> viewModelScope.launch { doRefresh(successToast = "Orders refreshed") }

            is OrderListScreenContract.Event.SearchTextChanged -> setState { copy(searchText = event.text) }

            is OrderListScreenContract.Event.OpenOrder -> openOrder(event.orderId)
            is OrderListScreenContract.Event.ClearError -> clearError()
            else -> Unit // Other events are not yet implemented in this placeholder VM
        }
    }

    private suspend fun doRefresh(successToast: String) {
        setState { copy(isLoading = true, error = null) }
        val result = refreshOrders()
        result.fold(
            onSuccess = {
                setState { copy(isLoading = false) }
                setEffect { OrderListScreenContract.Effect.ShowToast(successToast) }
            },
            onFailure = { t ->
                val msg = t.message ?: "Failed to refresh orders. Please try again."
                setState { copy(isLoading = false, error = msg) }
                setEffect { OrderListScreenContract.Effect.ShowError(msg) }
            }
        )
    }

    private fun openOrder(orderId: String) {
        setEffect { OrderListScreenContract.Effect.Outcome.OrderSelected(orderId) }
    }

    private fun clearError() {
        setState { copy(error = null) }
    }
}
