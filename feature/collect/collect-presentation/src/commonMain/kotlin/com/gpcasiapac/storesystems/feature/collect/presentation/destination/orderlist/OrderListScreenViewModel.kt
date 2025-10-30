package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.feedback.haptic.HapticEffect
import com.gpcasiapac.storesystems.common.feedback.sound.SoundEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandler
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandlerDelegate
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
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.DeleteWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.EnsureAndApplyOrderSelectionDeltaUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract.Effect.Outcome.Back
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract.Effect.Outcome.Logout
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract.Effect.Outcome.OrderSelected
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper.toListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.FilterChip
import com.gpcasiapac.storesystems.feature.collect.presentation.selection.SelectionCommitResult
import com.gpcasiapac.storesystems.feature.collect.presentation.selection.SelectionHandler
import com.gpcasiapac.storesystems.feature.collect.presentation.selection.SelectionHandlerDelegate
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
    private val deleteWorkOrderUseCase: DeleteWorkOrderUseCase,
    private val checkOrderExistsUseCase: CheckOrderExistsUseCase,
    private val collectSessionIdsFlowUseCase: GetCollectSessionIdsFlowUseCase,
    private val ensureAndApplyOrderSelectionDeltaUseCase: EnsureAndApplyOrderSelectionDeltaUseCase
) : MVIViewModel<
        OrderListScreenContract.Event,
        OrderListScreenContract.State,
        OrderListScreenContract.Effect>(),
    SessionHandlerDelegate<CollectSessionIds> by SessionHandler(
        initialSession = CollectSessionIds(),
        sessionFlow = collectSessionIdsFlowUseCase()
    ),
    SelectionHandlerDelegate by SelectionHandler() {

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

        bindSelectionHandler()

        // No eager work order creation here; only ensure/create when user actually adds/commits selections.
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
                viewModelScope.launch { fetchOrderList(successToast = "Orders refreshed") }
            }

            is OrderListScreenContract.Event.OpenOrder -> {
                setEffect { OrderSelected(event.orderId) }
            }

            is OrderListScreenContract.Event.ScanInvoice -> {
                val invoice = event.invoiceNumber.trim()
                setEffect { OrderListScreenContract.Effect.CollapseSearchBar }
                viewModelScope.launch {
                    when (val result = checkOrderExistsUseCase(invoice)) {
                        is CheckOrderExistsUseCase.UseCaseResult.Exists -> setEffect {
                            OrderSelected(
                                result.invoiceNumber
                            )
                        }

                        is CheckOrderExistsUseCase.UseCaseResult.Error -> {
                            setEffect { OrderListScreenContract.Effect.PlayHaptic(HapticEffect.Error) }
                            setEffect { OrderListScreenContract.Effect.PlaySound(SoundEffect.Error) }
                            setEffect { OrderListScreenContract.Effect.ShowSnackbar(result.message) }
                        }
                    }
                }
            }

            is OrderListScreenContract.Event.ClearError -> setState { copy(error = null) }
            is OrderListScreenContract.Event.ToggleCustomerType -> handleToggleCustomerType(
                event.type,
                event.checked
            )

            is OrderListScreenContract.Event.ApplyFilters -> handleApplyFilters(event.filterChipList)
            is OrderListScreenContract.Event.RemoveFilterChip -> handleRemoveFilterChip(event.filterChipList)
            is OrderListScreenContract.Event.ResetFilters -> handleResetFilters()
            is OrderListScreenContract.Event.SortChanged -> setState {
                copy(
                    filters = filters.copy(
                        sortOption = event.sortOption
                    )
                )
            }

            is OrderListScreenContract.Event.Back -> setEffect { Back }
            is OrderListScreenContract.Event.Logout -> setEffect { Logout }
            is OrderListScreenContract.Event.OpenHistory -> setEffect { OrderListScreenContract.Effect.Outcome.OpenHistory }

            is OrderListScreenContract.Event.Selection -> handleSelection(event.event)

            is OrderListScreenContract.Event.CloseFilterSheet -> setState { copy(isFilterSheetOpen = false) }
            is OrderListScreenContract.Event.DismissSnackbar -> { /* no-op */
            }

            is OrderListScreenContract.Event.OpenFilterSheet -> setState { copy(isFilterSheetOpen = true) }
            is OrderListScreenContract.Event.SubmitOrder -> setEffect { OrderSelected(event.orderId) }
            is OrderListScreenContract.Event.StartNewWorkOrderClicked -> handleStartNewWorkOrderClick()
            is OrderListScreenContract.Event.DraftBarDeleteClicked -> {
                viewModelScope.launch { handleDraftBarDeleteClicked() }
            }

            is OrderListScreenContract.Event.DraftBarViewClicked -> {
                val ids = viewState.value.selection.existing.toList()
                if (ids.isNotEmpty()) setEffect { OrderListScreenContract.Effect.Outcome.OrdersSelected }
            }

            is OrderListScreenContract.Event.ConfirmSearchSelection -> {
                setEffect { OrderListScreenContract.Effect.ShowSearchMultiSelectConfirmDialog() }
            }
        }
    }


    private suspend fun handleDraftBarDeleteClicked() {
        val workOrderId: WorkOrderId = sessionState.value.workOrderId.handleNull() ?: return
        deleteWorkOrderUseCase(workOrderId = workOrderId)
        setState {
            copy(
                isDraftBarVisible = false,
                selection = selection.copy(existing = emptySet()),
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
                    copy(
                        orders = items,
                        isLoading = false,
                        error = null,
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
                val sel = selection
                val showBar = persistedSet.isNotEmpty() && !sel.isEnabled
                copy(
                    selection = sel.copy(existing = persistedSet),
                    isDraftBarVisible = showBar
                )
            }
        }
    }

    private fun bindSelectionHandler() {
        // Bind shared selection controller to visible ids and mirror into state
        bindSelection(
            scope = viewModelScope,
            visibleIds = viewState.map { s -> s.orders.map { it.invoiceNumber }.toSet() },
            setSelection = { sel ->
                setState { copy(selection = sel) }
            },
            loadPersisted = {
                val workOrderId = sessionState.value.workOrderId
                if (workOrderId == null) emptySet() else observeSelectedOrderListUseCase(workOrderId).first()
            },
            commit = { toAdd, toRemove ->
                val session = sessionState.value
                when (
                    val result = ensureAndApplyOrderSelectionDeltaUseCase(
                        userId = session.userId,
                        currentSelectedWorkOrderId = session.workOrderId,
                        toAdd = toAdd,
                        toRemove = toRemove,
                    )
                ) {
                    is EnsureAndApplyOrderSelectionDeltaUseCase.UseCaseResult.Error -> SelectionCommitResult.Error(
                        result.message
                    )

                    is EnsureAndApplyOrderSelectionDeltaUseCase.UseCaseResult.Noop -> SelectionCommitResult.Noop
                    is EnsureAndApplyOrderSelectionDeltaUseCase.UseCaseResult.Summary -> SelectionCommitResult.Success
                }
            },
            onRequestConfirmDialog = {
                setEffect { OrderListScreenContract.Effect.ShowMultiSelectConfirmDialog() }
            },
            onConfirmProceed = {
                setEffect { OrderListScreenContract.Effect.Outcome.OrdersSelected }
            }
        )
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

    private fun handleConfirmSelection() {
        setEffect { OrderListScreenContract.Effect.ShowMultiSelectConfirmDialog() }
    }


    private fun handleStartNewWorkOrderClick() {
        // No DB work is required here; navigating to the fulfilment/details screen
        // will handle creation/ensure when the user actually adds something.
        setEffect { OrderListScreenContract.Effect.Outcome.OrdersSelected }
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
