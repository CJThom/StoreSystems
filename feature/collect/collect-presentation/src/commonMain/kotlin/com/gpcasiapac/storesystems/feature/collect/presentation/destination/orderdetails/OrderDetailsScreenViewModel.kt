package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetails

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.GetCollectOrderWithCustomerWithLineItemsFlowUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper.toState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OrderDetailsScreenViewModel(
    private val getCollectOrderWithCustomerWithLineItemsFlowUseCase: GetCollectOrderWithCustomerWithLineItemsFlowUseCase,
    private val invoiceNumber: String
) : MVIViewModel<
        OrderDetailsScreenContract.Event,
        OrderDetailsScreenContract.State,
        OrderDetailsScreenContract.Effect>() {

    override fun setInitialState(): OrderDetailsScreenContract.State {
        return OrderDetailsScreenContract.State(
            order = null,
            isLoading = true,
            error = null
        )
    }

    override fun onStart() {
        handleEvents(OrderDetailsScreenContract.Event.LoadOrder(invoiceNumber))
    }

    override fun handleEvents(event: OrderDetailsScreenContract.Event) {
        when (event) {
            is OrderDetailsScreenContract.Event.LoadOrder -> {
                loadOrderDetails(event.invoiceNumber)
            }
            is OrderDetailsScreenContract.Event.Back -> {
                setEffect { OrderDetailsScreenContract.Effect.Outcome.Back }
            }
        }
    }

    private fun loadOrderDetails(invoiceNumber: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            getCollectOrderWithCustomerWithLineItemsFlowUseCase(invoiceNumber)
                .catch { throwable ->
                    val errorMessage = throwable.message ?: "An unknown error occurred"
                    setState { copy(isLoading = false, error = errorMessage) }
                    setEffect { OrderDetailsScreenContract.Effect.ShowError(errorMessage) }
                }
                .collect { order ->
                    if (order != null) {
                        setState {
                            copy(
                                order = order.toState(),
                                isLoading = false
                            )
                        }
                    } else {
                        val errorMessage = "Order not found"
                        setState { copy(isLoading = false, error = errorMessage) }
                        setEffect { OrderDetailsScreenContract.Effect.ShowError(errorMessage) }
                    }
                }
        }
    }
}