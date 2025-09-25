package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.collect.presentation.model.Order
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OrderListScreenViewModel : MVIViewModel<OrderListScreenContract.Event, OrderListScreenContract.State, OrderListScreenContract.Effect>() {

    override fun setInitialState(): OrderListScreenContract.State = OrderListScreenContract.State(
        orders = emptyList(),
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
        // Load initial orders
        viewModelScope.launch {
            loadOrders(
                onSuccess = { setEffect { OrderListScreenContract.Effect.ShowToast("Orders loaded") } },
                onError = { message -> setEffect { OrderListScreenContract.Effect.ShowError(message) } }
            )
        }
    }

    // TABLE OF CONTENTS - All possible events handled here
    override fun handleEvents(event: OrderListScreenContract.Event) {
        when (event) {
            is OrderListScreenContract.Event.Load -> viewModelScope.launch {
                loadOrders(
                    onSuccess = { setEffect { OrderListScreenContract.Effect.ShowToast("Orders loaded") } },
                    onError = { message -> setEffect { OrderListScreenContract.Effect.ShowError(message) } }
                )
            }

            is OrderListScreenContract.Event.Refresh -> viewModelScope.launch {
                loadOrders(
                    onSuccess = { setEffect { OrderListScreenContract.Effect.ShowToast("Orders refreshed") } },
                    onError = { message -> setEffect { OrderListScreenContract.Effect.ShowError(message) } }
                )
            }

            is OrderListScreenContract.Event.OpenOrder -> openOrder(event.orderId)
            is OrderListScreenContract.Event.ClearError -> clearError()
        }
    }

    private suspend fun loadOrders(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        setState { copy(isLoading = true, error = null) }
        try {
            // Simulate network delay and produce demo orders
            delay(500)
            val demo = (1..10).map { idx ->
                Order(
                    id = "ORD-$idx",
                    title = "Order #$idx",
                )
            }
            setState { copy(orders = demo, isLoading = false, error = null) }
            onSuccess()
        } catch (t: Throwable) {
            val message = t.message ?: "Failed to load orders. Please try again."
            setState { copy(isLoading = false, error = message) }
            onError(message)
        }
    }

    private fun openOrder(orderId: String) {
        setEffect { OrderListScreenContract.Effect.Outcome.OrderSelected(orderId) }
    }

    private fun clearError() {
        setState { copy(error = null) }
    }
}
