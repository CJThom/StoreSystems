package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.flow.QueryFlow
import com.gpcasiapac.storesystems.common.presentation.flow.SearchDebounce
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestionType
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.FetchOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.GetOrderSearchSuggestionListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.FilterChip
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

    override fun setInitialState(): OrderListScreenContract.State {
        return OrderListScreenContract.State(
            orderList = emptyList(),
            filteredOrderList = emptyList(),
            isLoading = true,
            isRefreshing = false,
            searchText = "",
            isSearchActive = false,
            orderSearchSuggestions = emptyList(),
            customerTypeFilters = setOf(CustomerType.B2B, CustomerType.B2C),
            appliedFilterChips = emptyList(),
            isFilterSheetOpen = false,
            sortOption = SortOption.TIME_WAITING_DESC,
            isMultiSelectionEnabled = false,
            selectedOrderIdList = emptySet(),
            isSelectAllChecked = false,
            orderCount = 0,
            isSubmitting = false,
            submittedOrder = null,
            searchHintResultList = emptyList(),
            error = null,
        )
    }

    override suspend fun awaitReadiness(): Boolean {
        // Order list screen doesn't require session readiness for this placeholder
        return true
    }

    override fun handleReadinessFailed() {
        // Not applicable for this screen as readiness is always true
    }

    override fun onStart() {

        viewModelScope.launch {
            observeOrderList()
        }

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
                setState {
                    copy(
                        searchText = event.text,
                        // Let suggestions pipeline update; clear immediately if blank
                        orderSearchSuggestions = if (event.text.isBlank()) emptyList() else orderSearchSuggestions
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

            is OrderListScreenContract.Event.ToggleCustomerType -> {
                setState {
                    val updated =
                        if (event.checked) customerTypeFilters + event.type else customerTypeFilters - event.type
                    val newState = copy(customerTypeFilters = updated)
                    newState.copy(filteredOrderList = applyFiltersTo(orderList, newState))
                }
            }

            is OrderListScreenContract.Event.ApplyFilters -> {
                setState {
                    val merged =
                        (appliedFilterChips + event.chips).distinctBy { it.type to it.value }
                    val newState = copy(appliedFilterChips = merged)
                    newState.copy(filteredOrderList = applyFiltersTo(orderList, newState))
                }
            }

            is OrderListScreenContract.Event.RemoveFilterChip -> {
                setState {
                    val newList =
                        appliedFilterChips.filterNot { it.type == event.chip.type && it.value == event.chip.value }
                    val newState = copy(appliedFilterChips = newList)
                    newState.copy(filteredOrderList = applyFiltersTo(orderList, newState))
                }
            }

            is OrderListScreenContract.Event.ResetFilters -> {
                setState {
                    val newState = copy(appliedFilterChips = emptyList())
                    newState.copy(filteredOrderList = applyFiltersTo(orderList, newState))
                }
            }

            is OrderListScreenContract.Event.SortChanged -> {
                setState { copy(sortOption = event.option) }
            }

            else -> Unit
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
                val newStateBase = copy(
                    orderList = orders,
                    orderCount = orders.size,
                    isLoading = false,
                    error = null,
                )
                val filtered = applyFiltersTo(orders, newStateBase)
                newStateBase.copy(filteredOrderList = filtered)
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

    private fun applyFiltersTo(
        orders: List<Order>,
        state: OrderListScreenContract.State,
    ): List<Order> {
        // Base: customer type filter
        var result = orders.filter { it.customerType in state.customerTypeFilters }

        // Apply chips as AND conditions
        val chips = state.appliedFilterChips
        if (chips.isNotEmpty()) {
            result = result.filter { order ->
                chips.all { chip ->
                    when (chip.type) {
                        OrderSearchSuggestionType.NAME ->
                            order.customerName.contains(chip.value, ignoreCase = true)

                        OrderSearchSuggestionType.ORDER_NUMBER ->
                            order.invoiceNumber.contains(chip.value, ignoreCase = true) ||
                                    (order.webOrderNumber?.contains(
                                        chip.value,
                                        ignoreCase = true
                                    ) == true)

                        // No phone field on Order yet; keep as no-op so it doesn't exclude everything
                        OrderSearchSuggestionType.PHONE -> true
                    }
                }
            }
        }

        return result
    }

}
