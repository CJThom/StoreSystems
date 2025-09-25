package com.gpcasiapac.storesystems.feature.collect.presentation.orders

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OrdersViewModel : MVIViewModel<OrdersScreenContract.Event, OrdersScreenContract.State, OrdersScreenContract.Effect>() {

    override fun setInitialState(): OrdersScreenContract.State = OrdersScreenContract.State(
        orders = emptyList(),
        isLoading = true,
        error = null,
    )

    override fun onStart() {
        // Load initial orders
        viewModelScope.launch {
            loadOrders()
        }
    }

    override fun handleEvents(event: OrdersScreenContract.Event) {
        when (event) {
            is OrdersScreenContract.Event.Load -> viewModelScope.launch { loadOrders() }
            is OrdersScreenContract.Event.Refresh -> viewModelScope.launch { loadOrders() }
            is OrdersScreenContract.Event.OpenOrder -> openOrder(event.orderId)
            is OrdersScreenContract.Event.ClearError -> clearError()
        }
    }

    private suspend fun loadOrders() {
        setState { copy(isLoading = true, error = null) }
        // Simulate network delay and produce demo orders
        delay(500)
        val demo = (1..10).map { idx ->
            OrdersScreenContract.Order(
                id = "ORD-$idx",
                title = "Order #$idx",
            )
        }
        setState { copy(orders = demo, isLoading = false, error = null) }
    }

    private fun openOrder(orderId: String) {
        setEffect { OrdersScreenContract.Effect.Outcome.OrderSelected(orderId) }
    }

    private fun clearError() {
        setState { copy(error = null) }
    }
}
