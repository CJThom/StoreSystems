package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Clock

class OrderDetailScreenViewModel(

) : MVIViewModel<
        OrderDetailScreenContract.Event,
        OrderDetailScreenContract.State,
        OrderDetailScreenContract.Effect>() {

    override fun setInitialState(): OrderDetailScreenContract.State =
        OrderDetailScreenContract.State(
            orderId = "1", // TODO: get from usecase
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
                customerType = CustomerType.B2C,
                customerName = "Demo Customer",
                invoiceNumber = "INV-$orderId",
                webOrderNumber = null,
                pickedAt = Clock.System.now(),
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
