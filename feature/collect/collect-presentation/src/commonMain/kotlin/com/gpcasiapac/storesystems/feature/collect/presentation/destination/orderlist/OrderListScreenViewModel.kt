package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.flow.QueryFlow
import com.gpcasiapac.storesystems.common.presentation.flow.SearchDebounce
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestionType
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.domain.model.HapticType
import com.gpcasiapac.storesystems.feature.collect.domain.repository.MainOrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.CheckOrderExistsUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.FetchOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveMainOrdersUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveSearchOrdersUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveOrderCountUseCase
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class OrderListScreenViewModel(
    private val observeMainOrdersUseCase: ObserveMainOrdersUseCase,
    private val observeOrderCountUseCase: ObserveOrderCountUseCase,
    private val fetchOrderListUseCase: FetchOrderListUseCase,
    private val observeOrderSelectionUseCase: ObserveOrderSelectionUseCase,
    private val setOrderSelectionUseCase: SetOrderSelectionUseCase,
    private val addOrderSelectionUseCase: AddOrderSelectionUseCase,
    private val removeOrderSelectionUseCase: RemoveOrderSelectionUseCase,
    private val clearOrderSelectionUseCase: ClearOrderSelectionUseCase,
    private val checkOrderExistsUseCase: CheckOrderExistsUseCase,
) : MVIViewModel<OrderListScreenContract.Event, OrderListScreenContract.State, OrderListScreenContract.Effect>() {

    private val userRefId = "mock"

    override fun setInitialState(): OrderListScreenContract.State {

        val placeholders = CollectOrderListItemState.placeholderList(count = 10)
        
        return OrderListScreenContract.State(
            orders = placeholders,
            isLoading = true,
            isRefreshing = true,
            customerTypeFilterList = setOf(CustomerType.B2B, CustomerType.B2C),
            isFilterSheetOpen = false,
            sortOption = SortOption.TIME_WAITING_DESC,
            isMultiSelectionEnabled = false,
            selectedOrderIdList = emptySet(),
            isSelectAllChecked = false,
            existingDraftIdSet = emptySet(),
            pendingAddIdSet = emptySet(),
            pendingRemoveIdSet = emptySet(),
            isDraftBarVisible = false,
            orderCount = 0,
            isSubmitting = false,
            submittedCollectOrder = null,
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

        // Main list pipeline: respond to filter/sort changes
        viewModelScope.launch {
            viewState
                .map { state -> MainOrderQuery(state.customerTypeFilterList, state.sortOption) }
                .distinctUntilChanged()
                .flatMapLatest { query -> observeMainOrdersUseCase(query) }
                .map { list -> list.toListItemState() }
                .collectLatest { items ->
                    setState {
                        // maintain selection sanity against visible main list
                        val visibleIds = items.map { it.invoiceNumber }.toSet()
                        val newSelected =
                            if (isMultiSelectionEnabled) selectedOrderIdList.intersect(visibleIds) else emptySet()
                        val allSelected =
                            isMultiSelectionEnabled && visibleIds.isNotEmpty() && visibleIds.size == newSelected.size
                        copy(
                            orders = items,
                            isLoading = false,
                            error = null,
                            selectedOrderIdList = newSelected,
                            isSelectAllChecked = allSelected,
                        )
                    }
                }
        }


        viewModelScope.launch {
            // Observe total order count from DB (independent of filters/search)
            observeOrderCountUseCase().collectLatest { count ->
                setState { copy(orderCount = count) }
            }
        }

        viewModelScope.launch {
            delay(3000)
            fetchOrderList(successToast = "Orders loaded")
        }

        // Observe current draft selection to control the floating draft bar visibility
        // Delay slightly to avoid racing Room's initial database configuration on first open.
        viewModelScope.launch {
            delay(150)
            observeOrderSelectionUseCase(userRefId).collectLatest { persistedSet ->
                setState {
                    val showBar = persistedSet.isNotEmpty() && !isMultiSelectionEnabled
                    copy(
                        existingDraftIdSet = persistedSet,
                        isDraftBarVisible = showBar
                    )
                }
            }
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

            is OrderListScreenContract.Event.OpenOrder -> {
                // Single-tap now navigates to OrderDetails without persisting selection; selection happens on OrderDetails SELECT
                setEffect { OrderSelected(event.orderId) }
            }

            is OrderListScreenContract.Event.ScanInvoice -> {
                val invoice = event.invoiceNumber.trim()
                viewModelScope.launch {
                    when (val result = checkOrderExistsUseCase(invoice)) {
                        is CheckOrderExistsUseCase.UseCaseResult.Exists -> {
                            setEffect { OrderSelected(result.invoiceNumber) }
                        }
                        is CheckOrderExistsUseCase.UseCaseResult.Error.NotFound -> {
                            setEffect { OrderListScreenContract.Effect.Haptic(HapticType.Error) }
                            setEffect { OrderListScreenContract.Effect.PlayErrorSound }
                            setEffect { OrderListScreenContract.Effect.ShowSnackbar(result.message) }
                        }
                        is CheckOrderExistsUseCase.UseCaseResult.Error.InvalidInput -> {
                            setEffect { OrderListScreenContract.Effect.ShowSnackbar(result.message) }
                        }
                    }
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
            is OrderListScreenContract.Event.ConfirmSearchSelection -> {
                setEffect { OrderListScreenContract.Effect.ShowSearchMultiSelectConfirmDialog() }
            }
            is OrderListScreenContract.Event.ConfirmSelectionStay -> {
                onConfirmSelectionStay()
            }
            is OrderListScreenContract.Event.ConfirmSelectionProceed -> {
                onConfirmSelectionProceed()
            }
            is OrderListScreenContract.Event.DismissConfirmSelectionDialog -> {
                // no-op
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

            is OrderListScreenContract.Event.StartNewWorkOrderClicked -> {
                handleStartNewWorkOrderClick()
            }

            is OrderListScreenContract.Event.ToggleSelectionMode -> {
                handleToggleSelectionMode(event.enabled)
            }

            is OrderListScreenContract.Event.DraftBarDeleteClicked -> {
                viewModelScope.launch {
                    clearOrderSelectionUseCase(userRefId)
                    setState {
                        copy(
                            isDraftBarVisible = false,
                            existingDraftIdSet = emptySet(),
                            selectedOrderIdList = if (!isMultiSelectionEnabled) emptySet() else selectedOrderIdList
                        )
                    }
                }
            }

            is OrderListScreenContract.Event.DraftBarViewClicked -> {
                val ids = viewState.value.existingDraftIdSet.toList()
                if (ids.isNotEmpty()) {
                    setEffect { OrderListScreenContract.Effect.Outcome.OrdersSelected }
                } else {
                    setEffect { OrderListScreenContract.Effect.ShowToast("No draft to view") }
                }
            }

        }
    }


    private fun handleToggleCustomerType(type: CustomerType, checked: Boolean) {
        setState {
            val updated = customerTypeFilterList.toMutableSet().apply {
                if (checked) add(type) else remove(type)
            }
            copy(customerTypeFilterList = updated)
        }
    }

    private fun handleApplyFilters(filterChipList: List<FilterChip>) {
        // Chips removed; close the sheet only
        setState { copy(isFilterSheetOpen = false) }
    }

    private fun handleRemoveFilterChip(filterChip: FilterChip) {
        // No-op: chips removed; DB-side filters only
    }

    private fun handleResetFilters() {
        // No-op for now; could reset to default filters if needed
        setState { copy(isFilterSheetOpen = false) }
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
            )
        }
    }

    private fun handleConfirmSelection() {
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
            val visibleIds = orders.map { it.invoiceNumber }.toSet()
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
        val visibleIds = viewState.value.orders.map { it.invoiceNumber }.toSet()
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
            setEffect { OrderListScreenContract.Effect.Outcome.OrdersSelected }
        }
        setState {
            copy(
                isMultiSelectionEnabled = false,
                selectedOrderIdList = emptySet(),
                isSelectAllChecked = false
            )
        }
    }

    private fun handleStartNewWorkOrderClick() {
        viewModelScope.launch {
            setOrderSelectionUseCase(emptyList(), userRefId)
            setEffect { OrderListScreenContract.Effect.Outcome.OrdersSelected }
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
                    isSelectAllChecked = false
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
                    isSelectAllChecked = false
                )
            }
            setEffect { OrderListScreenContract.Effect.Outcome.OrdersSelected }
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
                        isSelectAllChecked = false
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
                    pendingRemoveIdSet = emptySet()
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
//                setEffect { OrderListScreenContract.Effect.ShowError(msg) }
            }
        )

    }



}
