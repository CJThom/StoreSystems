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
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.GetCollectOrderWithCustomerListFlowUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.GetOrderSearchSuggestionListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.AddOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.ClearOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.ObserveOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.RemoveOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.SetOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract.Effect.Outcome.Back
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract.Effect.Outcome.Logout
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract.Effect.Outcome.OrderSelected
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper.toListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.FilterChip
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OrderListScreenViewModel(
    private val getCollectOrderWithCustomerListFlowUseCase: GetCollectOrderWithCustomerListFlowUseCase,
    private val fetchOrderListUseCase: FetchOrderListUseCase,
    private val getOrderSearchSuggestionListUseCase: GetOrderSearchSuggestionListUseCase,
    private val observeOrderSelectionUseCase: ObserveOrderSelectionUseCase,
    private val setOrderSelectionUseCase: SetOrderSelectionUseCase,
    private val addOrderSelectionUseCase: AddOrderSelectionUseCase,
    private val removeOrderSelectionUseCase: RemoveOrderSelectionUseCase,
    private val clearOrderSelectionUseCase: ClearOrderSelectionUseCase,
) : MVIViewModel<OrderListScreenContract.Event, OrderListScreenContract.State, OrderListScreenContract.Effect>() {

    private val userRefId = "mock"

    override fun setInitialState(): OrderListScreenContract.State {

        val placeholders = CollectOrderListItemState.placeholderList(count = 10)
        
        return OrderListScreenContract.State(
            collectOrderListItemStateList = placeholders,
            filteredCollectOrderListItemStateList = placeholders,
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
            existingDraftIdSet = emptySet(),
            pendingAddIdSet = emptySet(),
            pendingRemoveIdSet = emptySet(),
            confirmSummary = null,
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
                    setOrderSelectionUseCase(listOf(event.orderId), userRefId)
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
            is OrderListScreenContract.Event.ConfirmSelectionStay -> {
                onConfirmSelectionStay()
            }
            is OrderListScreenContract.Event.ConfirmSelectionProceed -> {
                onConfirmSelectionProceed()
            }
            is OrderListScreenContract.Event.DismissConfirmSelectionDialog -> {
                setState { copy(confirmSummary = null) }
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
            val filtered = applyFiltersTo(collectOrderListItemStateList, base)
            base.copy(filteredCollectOrderListItemStateList = filtered)
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
            val filteredCollectOrderListItemStateList: List<CollectOrderListItemState> =
                applyFiltersTo(collectOrderListItemStateList, base)
            base.copy(filteredCollectOrderListItemStateList = filteredCollectOrderListItemStateList)
        }
    }

    private fun handleRemoveFilterChip(filterChip: FilterChip) {
        setState {
            val newFilterChipList: List<FilterChip> =
                appliedFilterChipList.filterNot { it == filterChip }
            val base = copy(appliedFilterChipList = newFilterChipList)
            val filteredCollectOrderListItemStateList: List<CollectOrderListItemState> =
                applyFiltersTo(collectOrderListItemStateList, base)
            base.copy(filteredCollectOrderListItemStateList = filteredCollectOrderListItemStateList)
        }
    }

    private fun handleResetFilters() {
        setState {
            val base = copy(appliedFilterChipList = emptyList())
            val filteredCollectOrderListItemStateList: List<CollectOrderListItemState> =
                applyFiltersTo(collectOrderListItemStateList, base)
            base.copy(filteredCollectOrderListItemStateList = filteredCollectOrderListItemStateList)
        }
    }

    private fun handleCancelSelection() {
        // Exit multi-select without touching the DB; clear in-memory pending sets
        setState {
            copy(
                isMultiSelectionEnabled = false,
                selectedOrderIdList = emptySet(),
                isSelectAllChecked = false,
                pendingAddIdSet = emptySet(),
                pendingRemoveIdSet = emptySet(),
                confirmSummary = null
            )
        }
    }

    private fun handleConfirmSelection() {
        val s = viewState.value
        val current = s.existingDraftIdSet.size
        val add = s.pendingAddIdSet.size
        val remove = s.pendingRemoveIdSet.size
        val projected = current - remove + add
        setState {
            copy(
                confirmSummary = OrderListScreenContract.ConfirmSummary(
                    currentCount = current,
                    addCount = add,
                    removeCount = remove,
                    projectedCount = projected,
                    addedPreview = s.pendingAddIdSet.take(5),
                    removedPreview = s.pendingRemoveIdSet.take(5),
                )
            )
        }
        setEffect { OrderListScreenContract.Effect.ShowMultiSelectConfirmDialog() }
    }

    private fun handleOrderChecked(orderId: String, checked: Boolean) {
        setState {
            var add = pendingAddIdSet.toMutableSet()
            var remove = pendingRemoveIdSet.toMutableSet()
            val persisted = existingDraftIdSet
            if (checked) {
                if (orderId in remove) {
                    remove.remove(orderId)
                } else if (orderId !in persisted) {
                    add.add(orderId)
                }
            } else {
                if (orderId in persisted) {
                    remove.add(orderId)
                } else {
                    add.remove(orderId)
                }
            }
            val selected = (persisted - remove) union add
            val visibleIds = filteredCollectOrderListItemStateList.map { it.invoiceNumber }.toSet()
            val allSelected = visibleIds.isNotEmpty() && visibleIds.all { it in selected }
            copy(
                pendingAddIdSet = add,
                pendingRemoveIdSet = remove,
                selectedOrderIdList = selected,
                isSelectAllChecked = allSelected
            )
        }
    }

    private fun handleSelectAll(checked: Boolean) {
        val current = viewState.value
        val visibleIds = current.filteredCollectOrderListItemStateList.map { it.invoiceNumber }.toSet()
        setState {
            var add = pendingAddIdSet.toMutableSet()
            var remove = pendingRemoveIdSet.toMutableSet()
            val persisted = existingDraftIdSet
            if (checked) {
                // Add all visible that are not already selected
                val currentlySelected = (persisted - remove) union add
                val toAdd = visibleIds - currentlySelected
                add.addAll(toAdd.filterNot { it in persisted })
                // If any visible were marked for removal, undo that
                remove.removeAll(visibleIds)
            } else {
                // Deselect: mark persisted visible for removal, drop any pending adds among visibles
                val persistedVisible = visibleIds.intersect(persisted)
                remove.addAll(persistedVisible)
                add.removeAll(visibleIds)
            }
            val selected = (persisted - remove) union add
            copy(
                pendingAddIdSet = add,
                pendingRemoveIdSet = remove,
                selectedOrderIdList = selected,
                isSelectAllChecked = checked
            )
        }
    }

    private fun handleSubmitSelectedOrders() {
        val selectedOrderIdList = viewState.value.selectedOrderIdList.toList()
        viewModelScope.launch {
            setOrderSelectionUseCase(selectedOrderIdList, userRefId)
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

    private fun onConfirmSelectionStay() {
        val s = viewState.value
        val toAdd = s.pendingAddIdSet
        val toRemove = s.pendingRemoveIdSet
        if (toAdd.isEmpty() && toRemove.isEmpty()) {
            setEffect { OrderListScreenContract.Effect.ShowToast("Nothing to update") }
            setState { copy(confirmSummary = null) }
            return
        }
        viewModelScope.launch {
            // Commit adds and removals
            toAdd.forEach { addOrderSelectionUseCase(it, userRefId) }
            toRemove.forEach { removeOrderSelectionUseCase(it, userRefId) }
            val newExisting = (s.existingDraftIdSet + toAdd) - toRemove
            val selected = newExisting
            setState {
                copy(
                    isMultiSelectionEnabled = false,
                    existingDraftIdSet = newExisting,
                    pendingAddIdSet = emptySet(),
                    pendingRemoveIdSet = emptySet(),
                    selectedOrderIdList = emptySet(),
                    isSelectAllChecked = false,
                    confirmSummary = null
                )
            }
            setEffect { OrderListScreenContract.Effect.ShowToast("Selection saved") }
        }
    }

    private fun onConfirmSelectionProceed() {
        val s = viewState.value
        val toAdd = s.pendingAddIdSet
        val toRemove = s.pendingRemoveIdSet
        viewModelScope.launch {
            toAdd.forEach { addOrderSelectionUseCase(it, userRefId) }
            toRemove.forEach { removeOrderSelectionUseCase(it, userRefId) }
            val finalIds = ((s.existingDraftIdSet + toAdd) - toRemove).toList()
            setState {
                copy(
                    isMultiSelectionEnabled = false,
                    existingDraftIdSet = finalIds.toSet(),
                    pendingAddIdSet = emptySet(),
                    pendingRemoveIdSet = emptySet(),
                    selectedOrderIdList = emptySet(),
                    isSelectAllChecked = false,
                    confirmSummary = null
                )
            }
            setEffect { OrderListScreenContract.Effect.Outcome.OrdersSelected(finalIds) }
        }
    }

    private fun handleToggleSelectionMode(enabled: Boolean) {
        if (enabled) {
            viewModelScope.launch {
                val persisted = observeOrderSelectionUseCase(userRefId).first()
                setState {
                    copy(
                        isMultiSelectionEnabled = true,
                        existingDraftIdSet = persisted,
                        pendingAddIdSet = emptySet(),
                        pendingRemoveIdSet = emptySet(),
                        selectedOrderIdList = persisted,
                        isSelectAllChecked = false,
                        confirmSummary = null
                    )
                }
            }
        } else {
            setState {
                copy(
                    isMultiSelectionEnabled = false,
                    selectedOrderIdList = emptySet(),
                    isSelectAllChecked = false,
                    pendingAddIdSet = emptySet(),
                    pendingRemoveIdSet = emptySet(),
                    confirmSummary = null
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

        // TODO: Query stuff
        queryFlow.flatMapLatest { query ->
            getCollectOrderWithCustomerListFlowUseCase()
        }.collectLatest { orders ->
            val collectOrderStateList = orders.toListItemState()
            setState {
                val newStateBase = copy(
                    collectOrderListItemStateList = collectOrderStateList,
                    orderCount = orders.size,
                    isLoading = false,
                    error = null,
                )
                val filtered = applyFiltersTo(collectOrderStateList, newStateBase)
                // Maintain selection consistency when list changes
                val filteredIds = filtered.map { it.invoiceNumber }.toSet()
                val newSelected =
                    if (isMultiSelectionEnabled) selectedOrderIdList.intersect(filteredIds) else emptySet()
                val allSelected =
                    isMultiSelectionEnabled && filteredIds.isNotEmpty() && filteredIds.size == newSelected.size
                newStateBase.copy(
                    filteredCollectOrderListItemStateList = filtered,
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
        collectOrderListItemStateList: List<CollectOrderListItemState>,
        state: OrderListScreenContract.State,
    ): List<CollectOrderListItemState> {
        // Base: customer type filter
        var result =
            collectOrderListItemStateList.filter { it.customerType in state.customerTypeFilterList }

        // Apply chips as AND conditions
        val chips = state.appliedFilterChipList
        if (chips.isNotEmpty()) {
            result = result.filter { order ->
                chips.all { chip ->
                    when (chip.type) {
                        OrderSearchSuggestionType.NAME -> {
                            order.customerName.contains(chip.value, ignoreCase = true)
                        }

                        OrderSearchSuggestionType.ORDER_NUMBER -> {
                            order.invoiceNumber.contains(chip.value, ignoreCase = true) ||
                                    (order.webOrderNumber?.contains(
                                        chip.value,
                                        ignoreCase = true
                                    ) == true)
                        }

                        // No phone field on Order yet; keep as no-op so it doesn't exclude everything
                        OrderSearchSuggestionType.PHONE -> true
                    }
                }
            }
        }

        return result
    }

}
