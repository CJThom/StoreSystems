package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.flow.QueryFlow
import com.gpcasiapac.storesystems.common.presentation.flow.SearchDebounce
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.collect.domain.repo.OrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.FetchOrderListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OrderListScreenViewModel(
    private val observeOrderListUseCase: ObserveOrderListUseCase,
    private val fetchOrderListUseCase: FetchOrderListUseCase,
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
        // Start long-lived observation of the order list based on the current query
        viewModelScope.launch {
            observeOrderList()
        }

        // Trigger initial refresh to seed data
        viewModelScope.launch {
            fetchOrderList(successToast = "Orders loaded")
        }

    }

    // TABLE OF CONTENTS - All possible events handled here
    override fun handleEvents(event: OrderListScreenContract.Event) {
        when (event) {
            is OrderListScreenContract.Event.Refresh -> {
                viewModelScope.launch {
                    fetchOrderList(successToast = "Orders refreshed")
                }
            }

            is OrderListScreenContract.Event.SearchTextChanged -> {
                setState { copy(searchText = event.text) }
            }

            is OrderListScreenContract.Event.OpenOrder -> {
                openOrder(event.orderId)
            }

            is OrderListScreenContract.Event.ClearError -> {
                clearError()
            }

            else -> Unit // Other events are not yet implemented in this placeholder VM
        }
    }

    private suspend fun fetchOrderList(successToast: String) {

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
                setEffect { OrderListScreenContract.Effect.ShowToast(successToast) }
            },
            onFailure = { t ->
                val msg = t.message ?: "Failed to refresh orders. Please try again."
                setState { copy(isLoading = false, error = msg) }
                setEffect { OrderListScreenContract.Effect.ShowError(msg) }
            }
        )

    }

    private suspend fun observeOrderList() {

        val queryFlow: Flow<OrderQuery> = QueryFlow.build(
            input = viewState.map { viewState ->
                OrderQuery(viewState.searchText)
            },
            debounce = SearchDebounce(millis = 150),
            keySelector = { query ->
                query.searchText
            }
        )

        queryFlow.flatMapLatest { query ->
            observeOrderListUseCase(query)
        }.collectLatest { orders ->
            setState {
                copy(
                    orderList = orders,
                    orderCount = orders.size,
                    isLoading = false,
                    error = null
                )
            }
        }

    }

    private fun openOrder(orderId: String) {
        setEffect { OrderListScreenContract.Effect.Outcome.OrderSelected(orderId) }
    }

    private fun clearError() {
        setState { copy(error = null) }
    }

}
