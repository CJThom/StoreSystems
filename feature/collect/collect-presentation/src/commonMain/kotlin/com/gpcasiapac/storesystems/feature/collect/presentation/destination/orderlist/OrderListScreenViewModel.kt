package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

class OrderListScreenViewModel : MVIViewModel<OrderListScreenContract.Event, OrderListScreenContract.State, OrderListScreenContract.Effect>() {

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
            else -> Unit // Other events are not yet implemented in this placeholder VM
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
            val now = Clock.System.now()
            val demo = (1..10).map { idx ->
                Order(
                    id = "ORD-$idx",
                    customerType = if (idx % 2 == 0) CustomerType.B2B else CustomerType.B2C,
                    customerName = if (idx % 2 == 0) "Acme Corp #$idx" else "John Smith #$idx",
                    invoiceNumber = "INV-${1000 + idx}",
                    webOrderNumber = "WEB-${2000 + idx}",
                    pickedAt = now - idx.hours, // demo: picked idx hours ago
                )
            }
            setState { copy(orderList = demo, isLoading = false, error = null) }
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
