package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.flow.QueryFlow
import com.gpcasiapac.storesystems.common.presentation.flow.SearchDebounce
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestionType
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.FetchOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.GetOrderSearchSuggestionListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.AddOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.ClearOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.ObserveOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.RemoveOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.SetOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract.Effect.Outcome.Back
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract.Effect.Outcome.Logout
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract.Effect.Outcome.OrderSelected
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper.toState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.FilterChip
import com.gpcasiapac.storesystems.feature.collect.presentation.fixture.CollectOrderPlaceholderData
import kotlinx.coroutines.delay
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
    private val observeOrderSelectionUseCase: ObserveOrderSelectionUseCase,
    private val setOrderSelectionUseCase: SetOrderSelectionUseCase,
    private val addOrderSelectionUseCase: AddOrderSelectionUseCase,
    private val removeOrderSelectionUseCase: RemoveOrderSelectionUseCase,
    private val clearOrderSelectionUseCase: ClearOrderSelectionUseCase,
) : MVIViewModel<OrderListScreenContract.Event, OrderListScreenContract.State, OrderListScreenContract.Effect>() {

    override fun setInitialState(): OrderListScreenContract.State {

        val placeholders = CollectOrderPlaceholderData.list(count = 10)
        
        return OrderListScreenContract.State(
            collectOrderStateList = placeholders,
            filteredCollectOrderStateList = placeholders,
            isLoading = true,
            isRefreshing = true,
            searchText = "",
            isSearchActive = false,
            orderSearchSuggestionList = emptyList(),
            customerTypeFilterList = setOf(CustomerType.B2B, CustomerType.B2C),
            appliedFilterChipList = emptyList(),
            isFilterSheetOpen = false,
            sortOption = SortOption.TIME_WAITING_DESC,
            isMultiSelectionEnabled = false,
            selectedOrderIdList = emptySet(),
            isSelectAllChecked = false,
            orderCount = 0,
            isSubmitting = false,
            submittedCollectOrder = null,
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
            delay(3000)
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

            is OrderListScreenContract.Event.SearchOnExpandedChange -> {
                handleSearchOnExpandedChange(event.expand)
            }

            is OrderListScreenContract.Event.ClearSearch -> {
                handleClearSearch()
            }

            is OrderListScreenContract.Event.SearchBarBackPressed -> {
                setEffect { OrderListScreenContract.Effect.CollapseSearchBar }
            }

            is OrderListScreenContract.Event.SearchResultClicked -> {
                handleSearchResultClicked(event.result)
            }

            is OrderListScreenContract.Event.SearchSuggestionClicked -> {
                handleSearchSuggestionClicked(event.suggestion)
            }

            is OrderListScreenContract.Event.OpenOrder -> {
                viewModelScope.launch {
                    setOrderSelectionUseCase(listOf(event.orderId))
                    setEffect { OrderSelected(event.orderId) }
                }
            }

            is OrderListScreenContract.Event.ClearError -> {
                setState { copy(error = null) }
            }

            is OrderListScreenContract.Event.ToggleCustomerType -> {
                handleToggleCustomerType(event.type, event.checked)
            }

            is OrderListScreenContract.Event.ApplyFilters -> {
                handleApplyFilters(event.filterChipList)
            }

            is OrderListScreenContract.Event.RemoveFilterChip -> {
                handleRemoveFilterChip(event.filterChipList)
            }

            is OrderListScreenContract.Event.ResetFilters -> {
                handleResetFilters()
            }

            is OrderListScreenContract.Event.SortChanged -> {
                setState { copy(sortOption = event.sortOption) }
            }

            is OrderListScreenContract.Event.Back -> {
                setEffect { Back }
            }

            is OrderListScreenContract.Event.Logout -> {
                setEffect { Logout }
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
                setEffect { OrderSelected(event.orderId) }
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
                orderSearchSuggestionList = if (text.isBlank()) emptyList() else orderSearchSuggestionList
            )
        }
    }

    private fun handleSearchOnExpandedChange(expand: Boolean) {
        if (expand) {
            setEffect { OrderListScreenContract.Effect.ExpandSearchBar }
        } else {
            setEffect { OrderListScreenContract.Effect.CollapseSearchBar }
        }
    }

    private fun handleClearSearch() {
        setState {
            copy(
                searchText = "",
                orderSearchSuggestionList = emptyList()
            )
        }
    }

    private fun handleSearchSuggestionClicked(suggestion: String) {
        setState {
            copy(
                searchText = suggestion,
                isSearchActive = false,
                orderSearchSuggestionList = emptyList()
            )
        }
    }

    private fun handleSearchResultClicked(result: String) {
        setState {
            copy(
                searchText = result,
                isSearchActive = false,
                orderSearchSuggestionList = emptyList()
            )
        }
    }

    private fun handleToggleCustomerType(type: CustomerType, checked: Boolean) {
        setState {
            val updated = customerTypeFilterList.toMutableSet().apply {
                if (checked) {
                    add(type)
                } else {
                    remove(type)
                }
            }
            val base = copy(customerTypeFilterList = updated)
            val filtered = applyFiltersTo(collectOrderStateList, base)
            base.copy(filteredCollectOrderStateList = filtered)
        }
    }

    private fun handleApplyFilters(filterChipList: List<FilterChip>) {
        setState {
            val newFilterChipList: List<FilterChip> =
                (appliedFilterChipList + filterChipList).distinctBy { it.type to it.value }
            val base = copy(
                appliedFilterChipList = newFilterChipList,
                isFilterSheetOpen = false
            )
            val filteredCollectOrderStateList: List<CollectOrderState> =
                applyFiltersTo(collectOrderStateList, base)
            base.copy(filteredCollectOrderStateList = filteredCollectOrderStateList)
        }
    }

    private fun handleRemoveFilterChip(filterChip: FilterChip) {
        setState {
            val newFilterChipList: List<FilterChip> =
                appliedFilterChipList.filterNot { it == filterChip }
            val base = copy(appliedFilterChipList = newFilterChipList)
            val filteredCollectOrderStateList: List<CollectOrderState> =
                applyFiltersTo(collectOrderStateList, base)
            base.copy(filteredCollectOrderStateList = filteredCollectOrderStateList)
        }
    }

    private fun handleResetFilters() {
        setState {
            val base = copy(appliedFilterChipList = emptyList())
            val filteredCollectOrderStateList: List<CollectOrderState> =
                applyFiltersTo(collectOrderStateList, base)
            base.copy(filteredCollectOrderStateList = filteredCollectOrderStateList)
        }
    }

    private fun handleCancelSelection() {
        viewModelScope.launch { clearOrderSelectionUseCase() }
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
        viewModelScope.launch {
            setOrderSelectionUseCase(selected)
            setEffect { OrderListScreenContract.Effect.Outcome.OrdersSelected(selected) }
        }
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
                if (checked) {
                    add(orderId)
                } else {
                    remove(orderId)
                }
            }
            val visibleIdList = filteredCollectOrderStateList.map { it.id }.toSet()
            val isAllSelected = visibleIdList.isNotEmpty() && visibleIdList.all { it in newSet }
            copy(
                selectedOrderIdList = newSet,
                isSelectAllChecked = isAllSelected
            )
        }
        viewModelScope.launch {
            if (checked) addOrderSelectionUseCase(orderId) else removeOrderSelectionUseCase(orderId)
        }
    }

    private fun handleSelectAll(checked: Boolean) {
        val current = viewState.value
        val visibleIdList = current.filteredCollectOrderStateList.map { it.id }
        val newSet = if (checked) {
            current.selectedOrderIdList + visibleIdList
        } else {
            current.selectedOrderIdList - visibleIdList.toSet()
        }
        viewModelScope.launch { setOrderSelectionUseCase(newSet.toList()) }
        setState {
            copy(
                selectedOrderIdList = newSet.toSet(),
                isSelectAllChecked = checked
            )
        }
    }

    private fun handleSubmitSelectedOrders() {
        val selectedOrderIdList = viewState.value.selectedOrderIdList.toList()
        viewModelScope.launch {
            setOrderSelectionUseCase(selectedOrderIdList)
            setEffect { OrderListScreenContract.Effect.Outcome.OrdersSelected(selectedOrderIdList) }
        }
        setState {
            copy(
                isMultiSelectionEnabled = false,
                selectedOrderIdList = emptySet(),
                isSelectAllChecked = false
            )
        }
    }

    private fun handleToggleSelectionMode(enabled: Boolean) {
        viewModelScope.launch { clearOrderSelectionUseCase() }
        setState {
            if (enabled) {
                copy(
                    isMultiSelectionEnabled = true,
                    selectedOrderIdList = emptySet(),
                    isSelectAllChecked = false
                )
            } else {
                copy(
                    isMultiSelectionEnabled = false,
                    selectedOrderIdList = emptySet(),
                    isSelectAllChecked = false
                )
            }
        }
    }
    // endregion

    private suspend fun fetchOrderList(successToast: String) {

        setState {
            copy(
                isRefreshing = true,
                error = null
            )
        }

        val result = fetchOrderListUseCase()

        result.fold(
            onSuccess = {
                setState { copy(isRefreshing = false) }
                setEffect { OrderListScreenContract.Effect.ShowToast(successToast) }
            },
            onFailure = { t ->
                val msg = t.message ?: "Failed to refresh orders. Please try again."
                setState { copy(isRefreshing = false, error = msg) }
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
            val collectOrderStateList = orders.toState()
            setState {
                val newStateBase = copy(
                    collectOrderStateList = collectOrderStateList,
                    orderCount = orders.size,
                    isLoading = false,
                    error = null,
                )
                val filtered = applyFiltersTo(collectOrderStateList, newStateBase)
                // Maintain selection consistency when list changes
                val filteredIds = filtered.map { it.id }.toSet()
                val newSelected =
                    if (isMultiSelectionEnabled) selectedOrderIdList.intersect(filteredIds) else emptySet()
                val allSelected =
                    isMultiSelectionEnabled && filteredIds.isNotEmpty() && filteredIds.size == newSelected.size
                newStateBase.copy(
                    filteredCollectOrderStateList = collectOrderStateList,
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
            if (!active || text.isBlank()) {
                emptyList()
            } else {
                getOrderSearchSuggestionListUseCase(text)
            }
        }.collectLatest { suggestions ->
            setState { copy(orderSearchSuggestionList = suggestions) }
        }

    }

    // TODO: Fix AI slop?
    private fun applyFiltersTo(
        collectOrderStateList: List<CollectOrderState>,
        state: OrderListScreenContract.State,
    ): List<CollectOrderState> {
        // Base: customer type filter
        var result =
            collectOrderStateList.filter { it.customerType in state.customerTypeFilterList }

        // Apply chips as AND conditions
        val chips = state.appliedFilterChipList
        if (chips.isNotEmpty()) {
            result = result.filter { order ->
                chips.all { chip ->
                    when (chip.type) {
                        OrderSearchSuggestionType.NAME -> {
                            order.customerName.contains(chip.value, ignoreCase = true)
                        }

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
