package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.flow.QueryFlow
import com.gpcasiapac.storesystems.common.presentation.flow.SearchDebounce
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.FetchOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.GetOrderSearchSuggestionListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveOrderListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

class OrderListScreenViewModel(
    private val observeOrderListUseCase: ObserveOrderListUseCase,
    private val fetchOrderListUseCase: FetchOrderListUseCase,
    private val getOrderSearchSuggestionListUseCase: GetOrderSearchSuggestionListUseCase,
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

        viewModelScope.launch {
            getOrderSearchSuggestionList()
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
                val newText = event.text
                setState {
                    copy(
                        searchText = newText,
                        // Let suggestions pipeline update; clear immediately if blank
                        orderSearchSuggestions = if (newText.isBlank()) emptyList() else orderSearchSuggestions
                    )
                }
            }

            is OrderListScreenContract.Event.SearchActiveChanged -> {
                setState {
                    copy(
                        isSearchActive = event.active,
                        orderSearchSuggestions = if (event.active) orderSearchSuggestions else emptyList()
                    )
                }
            }

            is OrderListScreenContract.Event.ClearSearch -> {
                setState {
                    copy(
                        searchText = "",
                        orderSearchSuggestions = emptyList()
                    )
                }
            }

            is OrderListScreenContract.Event.SearchSuggestionClicked -> {
                setState {
                    copy(
                        searchText = event.suggestion,
                        isSearchActive = false,
                        orderSearchSuggestions = emptyList()
                    )
                }
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
                    error = null,
                )
            }
        }

    }

    // Suggestions pipeline: debounce user input and fetch lightweight suggestions from repository
    private suspend fun getOrderSearchSuggestionList() {

        val activeTextFlow: Flow<Pair<String, Boolean>> =
            viewState.map { it.searchText to it.isSearchActive }

        QueryFlow.build(
            input = activeTextFlow,
            debounce = SearchDebounce(millis = 100),
            keySelector = { pair ->
                val (text, active) = pair
                if (active) text else ""
            }
        ).mapLatest { pair ->
            val (text, active) = pair
            if (!active || text.isBlank()) emptyList() else getOrderSearchSuggestionListUseCase(
                text
            )
        }.collectLatest { suggestions ->
            setState { copy(orderSearchSuggestions = suggestions) }
        }

    }

    private fun openOrder(orderId: String) {
        setEffect { OrderListScreenContract.Effect.Outcome.OrderSelected(orderId) }
    }

    private fun clearError() {
        setState { copy(error = null) }
    }


}
