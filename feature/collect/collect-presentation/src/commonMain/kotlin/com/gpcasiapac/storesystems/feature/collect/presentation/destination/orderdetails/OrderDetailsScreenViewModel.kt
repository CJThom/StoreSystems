package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetails

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.FetchOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.GetCollectOrderWithCustomerWithLineItemsFlowUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper.toState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OrderDetailsScreenViewModel(
    private val fetchOrderListUseCase: FetchOrderListUseCase,
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
        viewModelScope.launch {
            loadOrderDetails(invoiceNumber = invoiceNumber)
        }
    }

    override fun handleEvents(event: OrderDetailsScreenContract.Event) {
        when (event) {
            is OrderDetailsScreenContract.Event.Refresh -> {
                viewModelScope.launch {
                    fetchOrders(successToast = "Orders refreshed")
                }
            }

            is OrderDetailsScreenContract.Event.Back -> {
                setEffect { OrderDetailsScreenContract.Effect.Outcome.Back }
            }
        }
    }

    private suspend fun loadOrderDetails(invoiceNumber: String) {

        getCollectOrderWithCustomerWithLineItemsFlowUseCase(invoiceNumber).catch { throwable ->
            val errorMessage = throwable.message ?: "An unknown error occurred"
            setState { copy(isLoading = false, error = errorMessage) }
            setEffect { OrderDetailsScreenContract.Effect.ShowError(errorMessage) }
        }.collectLatest { order ->
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

    private suspend fun fetchOrders(successToast: String) {
        setState {
            copy(
                isLoading = true,
                error = null
            )
        }
        val result = fetchOrderListUseCase()
        result.fold(
            onSuccess = {
                setState { copy(isLoading = false) }
                setEffect { OrderDetailsScreenContract.Effect.ShowToast(successToast) }
            },
            onFailure = { t ->
                val msg = t.message ?: "Failed to refresh orders. Please try again."
                setState {
                    copy(
                        isLoading = false,
                        error = msg
                    )
                }
                setEffect { OrderDetailsScreenContract.Effect.ShowError(msg) }
            }
        )
    }


}