package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.feedback.haptic.HapticEffect
import com.gpcasiapac.storesystems.common.feedback.sound.SoundEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandler
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandlerDelegate
import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectSessionIds
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.MainOrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.CheckOrderExistsUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.FetchOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.ObserveMainOrdersUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.ObserveOrderCountUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.GetCollectSessionIdsFlowUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.SaveCollectUserPrefsUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.AddOrderListToCollectWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.AddOrderToCollectWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.CreateWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.DeleteWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.RemoveOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract.Effect.Outcome.Back
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract.Effect.Outcome.Logout
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract.Effect.Outcome.OrderSelected
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper.toListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.FilterChip
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class OrderListScreenViewModel(
    private val observeMainOrdersUseCase: ObserveMainOrdersUseCase,
    private val observeOrderCountUseCase: ObserveOrderCountUseCase,
    private val fetchOrderListUseCase: FetchOrderListUseCase,
    private val observeSelectedOrderListUseCase: ObserveOrderSelectionUseCase,
    private val addOrderListToCollectWorkOrderUseCase: AddOrderListToCollectWorkOrderUseCase,
    private val addOrderToCollectWorkOrderUseCase: AddOrderToCollectWorkOrderUseCase,
    private val removeOrderSelectionUseCase: RemoveOrderSelectionUseCase,
    private val deleteWorkOrderUseCase: DeleteWorkOrderUseCase,
    private val checkOrderExistsUseCase: CheckOrderExistsUseCase,
    private val collectSessionIdsFlowUseCase: GetCollectSessionIdsFlowUseCase,
    private val saveCollectUserPrefsUseCase: SaveCollectUserPrefsUseCase,
    private val createWorkOrderUseCase: CreateWorkOrderUseCase
) : MVIViewModel<
        OrderListScreenContract.Event,
        OrderListScreenContract.State,
        OrderListScreenContract.Effect>(),
    SessionHandlerDelegate<CollectSessionIds> by SessionHandler(
        initialSession = CollectSessionIds(),
        sessionFlow = collectSessionIdsFlowUseCase()
    ) {

    override fun setInitialState(): OrderListScreenContract.State {

        val placeholders = CollectOrderListItemState.placeholderList(count = 10)

        return OrderListScreenContract.State(
            orders = placeholders,
            isLoading = true,
            isRefreshing = true,
            filters = OrderListScreenContract.State.Filters(
                showB2B = true,
                showB2C = true,
                sortOption = SortOption.TIME_WAITING_DESC
            ),
            isFilterSheetOpen = false,
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
        val collectSessionIds = sessionState.first { it.userId != null }
        return collectSessionIds.userId != null
    }

    override fun handleReadinessFailed() {
        setState { copy(error = "userId: ${sessionState.value.userId}") }
    }

    override fun onStart() {
        viewModelScope.launch {

            val workOrderId = when (val result = createWorkOrderUseCase(userId = UserId("demo"))) {
                is CreateWorkOrderUseCase.UseCaseResult.Error.Unexpected -> null
                is CreateWorkOrderUseCase.UseCaseResult.Success -> result.workOrderId
            }

            saveCollectUserPrefsUseCase(
                userId = "demo",
                selectedWorkOrderId = workOrderId,
                isB2BFilterSelected = true,
                isB2CFilterSelected = true,
                sort = SortOption.TIME_WAITING_DESC
            )

        }

        viewModelScope.launch {
            observeOrderList()
        }

        viewModelScope.launch {
            delay(3000)
            fetchOrderList(successToast = "Orders loaded")
        }

        viewModelScope.launch {
            observeOrderCount()
        }

        viewModelScope.launch {
            observeOrderSelections()
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
                // Collapse search on scan (handled by UI via effect)
                setEffect { OrderListScreenContract.Effect.CollapseSearchBar }
                viewModelScope.launch {
                    when (val result = checkOrderExistsUseCase(invoice)) {
                        is CheckOrderExistsUseCase.UseCaseResult.Exists -> {
                            setEffect { OrderSelected(result.invoiceNumber) }
                        }

                        is CheckOrderExistsUseCase.UseCaseResult.Error -> {
                            setEffect { OrderListScreenContract.Effect.PlayHaptic(HapticEffect.Error) }
                            setEffect { OrderListScreenContract.Effect.PlaySound(SoundEffect.Error) }
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
                setState { copy(filters = filters.copy(sortOption = event.sortOption)) }
            }

            is OrderListScreenContract.Event.Back -> {
                setEffect { Back }
            }

            is OrderListScreenContract.Event.Logout -> {
                setEffect { Logout }
            }

            is OrderListScreenContract.Event.OpenHistory -> {
                setEffect { OrderListScreenContract.Effect.Outcome.OpenHistory }
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
                    handleDraftBarDeleteClicked()
                }
            }

            is OrderListScreenContract.Event.DraftBarViewClicked -> {
                val ids = viewState.value.existingDraftIdSet.toList()
                if (ids.isNotEmpty()) {
                    setEffect { OrderListScreenContract.Effect.Outcome.OrdersSelected }
                }
            }

        }
    }

    private suspend fun handleDraftBarDeleteClicked() {
        val workOrderId: WorkOrderId = sessionState.value.workOrderId.handleNull() ?: return
        deleteWorkOrderUseCase(workOrderId = workOrderId)
        setState {
            copy(
                isDraftBarVisible = false,
                existingDraftIdSet = emptySet(),
                selectedOrderIdList = if (!isMultiSelectionEnabled) emptySet() else selectedOrderIdList
            )
        }
    }


    // Main list pipeline: respond to filter/sort changes
    private suspend fun observeOrderList() {
        viewState
            .map { state ->
                val types = buildSet<CustomerType> {
                    if (state.filters.showB2B) add(CustomerType.B2B)
                    if (state.filters.showB2C) add(CustomerType.B2C)
                }
                MainOrderQuery(types, state.filters.sortOption)
            }
            .distinctUntilChanged()
            .flatMapLatest { query -> observeMainOrdersUseCase(query) }
            .map { list -> list.toListItemState() }
            .collectLatest { items ->
                setState {
                    // maintain selection sanity against visible main list
                    val visibleIds = items.map { it.invoiceNumber }.toSet()
                    val newSelected =
                        if (isMultiSelectionEnabled) selectedOrderIdList.intersect(
                            visibleIds
                        ) else emptySet()
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

    private suspend fun observeOrderCount() {
        // Observe total order count from DB (independent of filters/search)
        observeOrderCountUseCase().collectLatest { count ->
            setState { copy(orderCount = count) }
        }
    }


    // Observe current draft selection to control the floating draft bar visibility
    // Delay slightly to avoid racing Room's initial database configuration on first open.
    private suspend fun observeOrderSelections() {
        delay(150)
        sessionState.distinctUntilChangedBy { session ->
            session.workOrderId
        }.map { session ->
            session.workOrderId
        }.flatMapLatest { workOrderId ->
            if (workOrderId == null) {
                flowOf(emptySet())
            } else {
                observeSelectedOrderListUseCase(workOrderId = workOrderId)
            }
        }.collectLatest { persistedSet ->
            setState {
                val showBar = persistedSet.isNotEmpty() && !isMultiSelectionEnabled
                copy(
                    existingDraftIdSet = persistedSet,
                    isDraftBarVisible = showBar
                )
            }
        }
    }


    private fun handleToggleCustomerType(type: CustomerType, checked: Boolean) {
        setState {
            val newFilters = when (type) {
                CustomerType.B2B -> filters.copy(showB2B = checked)
                CustomerType.B2C -> filters.copy(showB2C = checked)
            }
            copy(filters = newFilters)
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
            val workOrderId: WorkOrderId =
                sessionState.value.workOrderId.handleNull() ?: return@launch

            addOrderListToCollectWorkOrderUseCase(
                workOrderId = workOrderId,
                orderIdList = selectedOrderIdList
            )

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

            val workOrderId: WorkOrderId =
                sessionState.value.workOrderId.handleNull() ?: return@launch

            addOrderListToCollectWorkOrderUseCase(
                workOrderId = workOrderId,
                orderIdList = emptyList()
            )

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
            return
        }
        viewModelScope.launch {
            val workOrderId: WorkOrderId =
                sessionState.value.workOrderId.handleNull() ?: return@launch
            // Commit adds and removals
            toAdd.forEach {
                addOrderToCollectWorkOrderUseCase(
                    workOrderId = workOrderId,
                    orderId = it
                )
            }
            toRemove.forEach {
                removeOrderSelectionUseCase(
                    workOrderId = workOrderId,
                    orderId = it
                )
            }
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
        }
    }

    private fun onConfirmSelectionProceed() {
        val s = viewState.value
        val toAdd = s.pendingAddIdSet
        val toRemove = s.pendingRemoveIdSet
        viewModelScope.launch {
            val workOrderId: WorkOrderId =
                sessionState.value.workOrderId.handleNull() ?: return@launch
            toAdd.forEach {
                addOrderToCollectWorkOrderUseCase(
                    workOrderId = workOrderId,
                    orderId = it
                )
            }
            toRemove.forEach {
                removeOrderSelectionUseCase(
                    workOrderId = workOrderId,
                    orderId = it
                )
            }
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
                val workOrderId: WorkOrderId? = sessionState.value.workOrderId.handleNull()

                val persisted = if (workOrderId == null) {
                    emptySet()
                } else {
                    observeSelectedOrderListUseCase(workOrderId = workOrderId).first()
                }

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

        when (val result = fetchOrderListUseCase()) {
            is FetchOrderListUseCase.UseCaseResult.Success -> {
                setState { copy(isRefreshing = false) }
            }

            is FetchOrderListUseCase.UseCaseResult.Error -> {
                val msg = result.message
                setState { copy(isRefreshing = false, error = msg) }
            }
        }

    }


    private fun WorkOrderId?.handleNull(): WorkOrderId? {
        if (this == null) {
            setState { copy(error = "No Work Order Selected") }
        }
        return this
    }


}
