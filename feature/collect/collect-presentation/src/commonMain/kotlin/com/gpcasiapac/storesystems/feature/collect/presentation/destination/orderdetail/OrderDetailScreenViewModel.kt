package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.collect.presentation.model.Order
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OrderDetailScreenViewModel : MVIViewModel<OrderDetailScreenContract.Event, OrderDetailScreenContract.State, OrderDetailScreenContract.Effect>() {

    override fun setInitialState(): OrderDetailScreenContract.State = OrderDetailScreenContract.State(
        orderId = null,
        order = null,
        isLoading = false,
        error = null,
    )

    override suspend fun awaitReadiness(): Boolean {
        // This placeholder screen has no special readiness requirement
        return true
    }

    override fun handleReadinessFailed() {
        // Not applicable in this placeholder
    }

    override fun onStart() {
        // Trigger initial load when viewState is first collected (per MVIViewModel contract)
        val id = viewState.value.orderId
        if (id != null) {
            viewModelScope.launch {
                loadOrder(
                    orderId = id,
                    onSuccess = { setEffect { OrderDetailScreenContract.Effect.ShowToast("Order $id loaded") } },
                    onError = { msg -> setEffect { OrderDetailScreenContract.Effect.ShowError(msg) } }
                )
            }
        }
    }

    /**
     * Provide navigation arguments before the first viewState collection so onStart can act on them.
     * Idempotent: setting the same id multiple times will be ignored.
     */
    fun setArgs(orderId: String) {
        if (viewState.value.orderId == orderId) return
        setState { copy(orderId = orderId) }
    }

    // TABLE OF CONTENTS - All possible events handled here
    override fun handleEvents(event: OrderDetailScreenContract.Event) {
        when (event) {
            is OrderDetailScreenContract.Event.LoadOrder -> viewModelScope.launch {
                loadOrder(
                    orderId = event.orderId,
                    onSuccess = { setEffect { OrderDetailScreenContract.Effect.ShowToast("Order ${'$'}{event.orderId} loaded") } },
                    onError = { msg -> setEffect { OrderDetailScreenContract.Effect.ShowError(msg) } }
                )
            }

            is OrderDetailScreenContract.Event.Refresh -> viewModelScope.launch {
                val id = viewState.value.orderId ?: return@launch
                loadOrder(
                    orderId = id,
                    onSuccess = { setEffect { OrderDetailScreenContract.Effect.ShowToast("Order refreshed") } },
                    onError = { msg -> setEffect { OrderDetailScreenContract.Effect.ShowError(msg) } }
                )
            }

            is OrderDetailScreenContract.Event.ClearError -> clearError()
            is OrderDetailScreenContract.Event.Back -> setEffect { OrderDetailScreenContract.Effect.Outcome.Back }
        }
    }

    private suspend fun loadOrder(
        orderId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        setState { copy(isLoading = true, error = null, orderId = orderId) }
        try {
            // Simulate network
            delay(300)
            val order = Order(
                id = orderId,
                title = "Order Details for ${'$'}orderId",
            )
            setState { copy(order = order, isLoading = false, error = null) }
            onSuccess()
        } catch (t: Throwable) {
            val message = t.message ?: "Failed to load order. Please try again."
            setState { copy(isLoading = false, error = message) }
            onError(message)
        }
    }

    private fun clearError() {
        setState { copy(error = null) }
    }
}
