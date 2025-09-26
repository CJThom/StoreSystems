package com.gpcasiapac.storesystems.feature.collect.presentation.orders

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OrdersViewModel :
    MVIViewModel<OrdersScreenContract.Event, OrdersScreenContract.State, OrdersScreenContract.Effect>() {

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
            is OrdersScreenContract.Event.MultiSelectionChanged -> setState {
                copy(
                    isMultiSelectionEnabled = event.isEnabled
                )
            }
        }
    }

    private suspend fun loadOrders() {
        setState { copy(isLoading = true, error = null) }
        // Simulate network delay and produce demo orders
        delay(500)
        val demo = (1..10).map { idx ->
            OrdersScreenContract.Order(
                id = "ORD-$idx",
                customerName = "<NAME>",
                orderNumber = "1234567890",
                phoneNumber = "9112312310",
                deliveryTime = "12:30 PM",
                isBusiness = idx % 2 == 0,
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
