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
                handleSearchTextChanged(event.text)
            }

            is OrderListScreenContract.Event.SearchActiveChanged -> {
                handleSearchActiveChanged(event.active)
            }

            is OrderListScreenContract.Event.ClearSearch -> {
                handleClearSearch()
            }

            is OrderListScreenContract.Event.SearchSuggestionClicked -> {
                handleSearchSuggestionClicked(event.suggestion)
            }

            is OrderListScreenContract.Event.OpenOrder -> {
                setEffect { OrderListScreenContract.Effect.Outcome.OrderSelected(event.orderId) }
            }

            is OrderListScreenContract.Event.ClearError -> {
                setState { copy(error = null) }
            }

            is OrderListScreenContract.Event.ToggleCustomerType -> {
                handleToggleCustomerType(event.type, event.checked)
            }

            is OrderListScreenContract.Event.ApplyFilters -> {
                handleApplyFilters(event.chips)
            }

            is OrderListScreenContract.Event.RemoveFilterChip -> {
                handleRemoveFilterChip(event.chip)
            }

            is OrderListScreenContract.Event.ResetFilters -> {
                handleResetFilters()
            }

            is OrderListScreenContract.Event.SortChanged -> {
                setState { copy(sortOption = event.option) }
            }

            is OrderListScreenContract.Event.Back -> {
                setEffect { OrderListScreenContract.Effect.Outcome.Back }
            }

            is OrderListScreenContract.Event.CancelSelection -> {
                handleCancelSelection()
            }

            is OrderListScreenContract.Event.CloseFilterSheet -> {
                setState { copy(isFilterSheetOpen = false) }
            }

            is OrderListScreenContract.Event.ConfirmSelection -> {
                handleConfirmSelection()
            }

            is OrderListScreenContract.Event.DismissSnackbar -> {

            }

            is OrderListScreenContract.Event.OpenFilterSheet -> {
                setState { copy(isFilterSheetOpen = true) }
            }

            is OrderListScreenContract.Event.OrderChecked -> {
                handleOrderChecked(event.orderId, event.checked)
            }

            is OrderListScreenContract.Event.SelectAll -> {
                handleSelectAll(event.checked)
            }

            is OrderListScreenContract.Event.SubmitOrder -> {
                setEffect { OrderListScreenContract.Effect.Outcome.OrderSelected(event.orderId) }
            }

            is OrderListScreenContract.Event.SubmitSelectedOrders -> {
                handleSubmitSelectedOrders()
            }

            is OrderListScreenContract.Event.ToggleSelectionMode -> {
                handleToggleSelectionMode(event.enabled)
            }
        }
    }

    private fun handleSearchTextChanged(text: String) {
        setState {
            copy(
                searchText = text,
                // Let suggestions pipeline update; clear immediately if blank
                orderSearchSuggestions = if (text.isBlank()) emptyList() else orderSearchSuggestions
            )
        }
    }

    private fun handleSearchActiveChanged(active: Boolean) {
        setState {
            copy(
                isSearchActive = active,
                orderSearchSuggestions = if (active) orderSearchSuggestions else emptyList()
            )
        }
    }

    private fun handleClearSearch() {
        setState {
            copy(
                searchText = "",
                orderSearchSuggestions = emptyList()
            )
        }
    }

    private fun handleSearchSuggestionClicked(suggestion: String) {
        setState {
            copy(
                searchText = suggestion,
                isSearchActive = false,
                orderSearchSuggestions = emptyList()
            )
        }
    }

    private fun handleToggleCustomerType(type: CustomerType, checked: Boolean) {
        setState {
            val updated = customerTypeFilters.toMutableSet().apply {
                if (checked) add(type) else remove(type)
            }
            val base = copy(customerTypeFilters = updated)
            val filtered = applyFiltersTo(orderList, base)
            base.copy(filteredOrderList = filtered)
        }
    }

    private fun handleApplyFilters(chips: List<FilterChip>) {
        setState {
            val newChips = (appliedFilterChips + chips).distinctBy { it.type to it.value }
            val base = copy(
                appliedFilterChips = newChips,
                isFilterSheetOpen = false
            )
            val filtered = applyFiltersTo(orderList, base)
            base.copy(filteredOrderList = filtered)
        }
    }

    private fun handleRemoveFilterChip(chip: FilterChip) {
        setState {
            val newChips = appliedFilterChips.filterNot { it == chip }
            val base = copy(appliedFilterChips = newChips)
            val filtered = applyFiltersTo(orderList, base)
            base.copy(filteredOrderList = filtered)
        }
    }

    private fun handleResetFilters() {
        setState {
            val base = copy(appliedFilterChips = emptyList())
            val filtered = applyFiltersTo(orderList, base)
            base.copy(filteredOrderList = filtered)
        }
    }

    private fun handleCancelSelection() {
        setState {
            copy(
                isMultiSelectionEnabled = false,
                selectedOrderIdList = emptySet(),
                isSelectAllChecked = false
            )
        }
    }

    private fun handleConfirmSelection() {
        val selected = viewState.value.selectedOrderIdList.toList()
        setEffect { OrderListScreenContract.Effect.Outcome.OrdersSelected(selected) }
        setState {
            copy(
                isMultiSelectionEnabled = false,
                selectedOrderIdList = emptySet(),
                isSelectAllChecked = false
            )
        }
    }

    private fun handleOrderChecked(orderId: String, checked: Boolean) {
        setState {
            val newSet = selectedOrderIdList.toMutableSet().apply {
                if (checked) add(orderId) else remove(orderId)
            }
            val visibleIds = filteredOrderList.map { it.id }.toSet()
            val allSelected = visibleIds.isNotEmpty() && visibleIds.all { it in newSet }
            copy(
                selectedOrderIdList = newSet,
                isSelectAllChecked = allSelected
            )
        }
    }

    private fun handleSelectAll(checked: Boolean) {
        setState {
            val visibleIds = filteredOrderList.map { it.id }
            val newSet =
                if (checked) selectedOrderIdList + visibleIds else selectedOrderIdList - visibleIds.toSet()
            copy(
                selectedOrderIdList = newSet.toSet(),
                isSelectAllChecked = checked
            )
        }
    }

    private fun handleSubmitSelectedOrders() {
        val selected = viewState.value.selectedOrderIdList.toList()
        setEffect { OrderListScreenContract.Effect.Outcome.OrdersSelected(selected) }
        setState {
            copy(
                isMultiSelectionEnabled = false,
                selectedOrderIdList = emptySet(),
                isSelectAllChecked = false
            )
        }
    }

    private fun handleToggleSelectionMode(enabled: Boolean) {
        setState {
            if (enabled) copy(
                isMultiSelectionEnabled = true,
                selectedOrderIdList = emptySet(),
                isSelectAllChecked = false
            )
            else copy(
                isMultiSelectionEnabled = false,
                selectedOrderIdList = emptySet(),
                isSelectAllChecked = false
            )
        }
    }
    // endregion

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
                // Maintain selection consistency when list changes
                val filteredIds = filtered.map { it.id }.toSet()
                val newSelected =
                    if (isMultiSelectionEnabled) selectedOrderIdList.intersect(filteredIds) else emptySet()
                val allSelected =
                    isMultiSelectionEnabled && filteredIds.isNotEmpty() && filteredIds.size == newSelected.size
                newStateBase.copy(
                    filteredOrderList = filtered,
                    selectedOrderIdList = newSelected,
                    isSelectAllChecked = allSelected
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
