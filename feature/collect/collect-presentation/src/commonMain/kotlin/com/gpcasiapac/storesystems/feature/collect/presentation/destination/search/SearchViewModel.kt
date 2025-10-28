package com.gpcasiapac.storesystems.feature.collect.presentation.destination.search

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.flow.QueryFlow
import com.gpcasiapac.storesystems.common.presentation.flow.SearchDebounce
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandler
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandlerDelegate
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectSessionIds
import com.gpcasiapac.storesystems.feature.collect.domain.model.SearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.search.GetOrderSearchSuggestionListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.search.ObserveSearchOrdersUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.GetCollectSessionIdsFlowUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.AddOrderListToCollectWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.AddOrderToCollectWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.DeleteWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.RemoveOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.EnsureWorkOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.UpdateSelectedWorkOrderIdUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper.toListItemState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

class SearchViewModel(
    private val observeSearchOrdersUseCase: ObserveSearchOrdersUseCase,
    private val getOrderSearchSuggestionListUseCase: GetOrderSearchSuggestionListUseCase,
    // Selection persistence dependencies
    private val observeOrderSelectionUseCase: ObserveOrderSelectionUseCase,
    private val addOrderListToCollectWorkOrderUseCase: AddOrderListToCollectWorkOrderUseCase,
    private val addOrderToCollectWorkOrderUseCase: AddOrderToCollectWorkOrderUseCase,
    private val removeOrderSelectionUseCase: RemoveOrderSelectionUseCase,
    private val deleteWorkOrderUseCase: DeleteWorkOrderUseCase,
    private val ensureWorkOrderSelectionUseCase: EnsureWorkOrderSelectionUseCase,
    private val updateSelectedWorkOrderIdUseCase: UpdateSelectedWorkOrderIdUseCase,
    private val collectSessionIdsFlowUseCase: GetCollectSessionIdsFlowUseCase
) : MVIViewModel<
        SearchContract.Event,
        SearchContract.State,
        SearchContract.Effect>(),
    SessionHandlerDelegate<CollectSessionIds> by SessionHandler(
        initialSession = CollectSessionIds(),
        sessionFlow = collectSessionIdsFlowUseCase()
    ) {

    override fun setInitialState(): SearchContract.State = SearchContract.State.empty()

    override suspend fun awaitReadiness(): Boolean = true

    override fun handleReadinessFailed() { /* no-op */
    }

    override fun onStart() {
        // Search results pipeline: immediate reset on blank, debounced for non-blank
        viewModelScope.launch {
            viewState
                .map { it.searchText to it.isSearchActive }
                .flatMapLatest { (text, active) ->
                    val t = text
                    when {
                        !active -> flowOf(emptyList())
                        t.isBlank() -> flowOf(emptyList()) // immediate clearing without debounce
                        else -> {
                            // Debounce only when non-blank to avoid stale UI on backspace-to-blank
                            QueryFlow.build(
                                input = flowOf(t),
                                debounce = SearchDebounce(millis = 150),
                                keySelector = { it }
                            ).flatMapLatest { q -> observeSearchOrdersUseCase(q) }
                        }
                    }
                }
                .map { list -> list.toListItemState() }
                .collectLatest { results ->
                    setState { copy(searchOrderItems = results) }
                }
        }

        // Suggestions pipeline: immediate defaults on blank when active, debounced for non-blank
        viewModelScope.launch {
            viewState
                .map { it.searchText to it.isSearchActive }
                .flatMapLatest { (text, active) ->
                    val t = text
                    when {
                        !active -> flowOf(emptyList())
                        t.isBlank() -> kotlinx.coroutines.flow.flow {
                            emit(getOrderSearchSuggestionListUseCase(""))
                        }

                        else -> {
                            QueryFlow.build(
                                input = flowOf(t),
                                debounce = SearchDebounce(millis = 100),
                                keySelector = { it }
                            ).mapLatest { q -> getOrderSearchSuggestionListUseCase(q) }
                        }
                    }
                }
                .collectLatest { suggestions ->
                    setState { copy(searchSuggestions = suggestions) }
                }
        }
    }

    override fun handleEvents(event: SearchContract.Event) {
        when (event) {
            is SearchContract.Event.SearchTextChanged -> handleSearchTextChanged(event.text)
            is SearchContract.Event.SearchOnExpandedChange -> handleSearchOnExpandedChange(event.expand)
            SearchContract.Event.ClearSearch -> handleClearSearch()
            SearchContract.Event.SearchBarBackPressed -> handleSearchOnExpandedChange(false)
            is SearchContract.Event.SearchResultClicked -> handleSearchResultClicked(event.result)
            is SearchContract.Event.SearchSuggestionClicked -> handleSearchSuggestionClicked(
                event.suggestion
            )

            is SearchContract.Event.TypedSuffixChanged -> handleTypedSuffixChanged(event.text)
            is SearchContract.Event.RemoveChip -> handleRemoveChip(event.suggestion)

            // Selection events
            is SearchContract.Event.ToggleSelectionMode -> handleToggleSelectionMode(event.enabled)
            is SearchContract.Event.OrderChecked -> handleOrderChecked(
                event.orderId,
                event.checked
            )

            is SearchContract.Event.SelectAll -> handleSelectAll(event.checked)
            SearchContract.Event.CancelSelection -> handleCancelSelection()
            SearchContract.Event.ConfirmSelection -> handleConfirmSelection()
            SearchContract.Event.ConfirmSelectionStay -> handleConfirmSelectionStay()
            SearchContract.Event.ConfirmSelectionProceed -> handleConfirmSelectionProceed()
            SearchContract.Event.DismissConfirmSelectionDialog -> { /* no-op for now */
            }
        }
    }

    private fun handleSearchTextChanged(text: String) {
        // Update the query; reactive pipelines handle clearing and default suggestions.
        setState { copy(searchText = text) }
    }

    private fun handleSearchOnExpandedChange(expand: Boolean) {
        if (!expand) {
            // On collapse, clear chips and typed suffix for a clean slate
            setState {
                copy(
                    isSearchActive = false,
                    selectedChips = emptyList(),
                    typedSuffix = "",
                    searchText = "",
                )
            }
            setEffect { SearchContract.Effect.CollapseSearchBar }
        } else {
            setState { copy(isSearchActive = true) }
            setEffect { SearchContract.Effect.ExpandSearchBar }
        }
    }

    private fun handleClearSearch() {
        setState { copy(searchText = "", selectedChips = emptyList(), typedSuffix = "") }
    }

    private fun handleSearchSuggestionClicked(suggestion: SearchSuggestion) {
        setState {
            val exists = selectedChips.any { it == suggestion }
            val newChips = if (exists) selectedChips else selectedChips + suggestion
            copy(
                selectedChips = newChips,
                typedSuffix = "",
                searchText = buildCombinedQuery(newChips, "")
            )
        }
    }

    private fun handleSearchResultClicked(result: String) {
        setState { copy(searchText = result, isSearchActive = false) }
        setEffect { SearchContract.Effect.CollapseSearchBar }
    }

    // ---------------- Selection handling ----------------
    private fun handleToggleSelectionMode(enabled: Boolean) {
        if (enabled) {
            viewModelScope.launch {
                val workOrderId: WorkOrderId =
                    sessionState.value.workOrderId.handleNull() ?: return@launch
                val persisted = observeOrderSelectionUseCase(workOrderId = workOrderId).first()
                setState {
                    copy(
                        isMultiSelectionEnabled = true,
                        existingDraftIdSet = persisted,
                        pendingAddIdSet = emptySet(),
                        pendingRemoveIdSet = emptySet(),
                        selectedOrderIdList = persisted,
                        isSelectAllChecked = false,
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
                )
            }
        }
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
            val visibleIds = searchOrderItems.map { it.invoiceNumber }.toSet()
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
        val visibleIds = viewState.value.searchOrderItems.map { it.invoiceNumber }.toSet()
        setState {
            var add = pendingAddIdSet.toMutableSet()
            var remove = pendingRemoveIdSet.toMutableSet()
            val persisted = existingDraftIdSet
            if (checked) {
                val currentlySelected = (persisted - remove) union add
                val toAdd = visibleIds - currentlySelected
                add.addAll(toAdd.filterNot { it in persisted })
                remove.removeAll(visibleIds)
            } else {
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

    private fun handleCancelSelection() {
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
        // Show dialog; actual commit happens based on user choice
        setEffect { SearchContract.Effect.ShowMultiSelectConfirmDialog() }
    }

    private fun handleConfirmSelectionStay() {
        val s = viewState.value
        val toAdd = s.pendingAddIdSet
        val toRemove = s.pendingRemoveIdSet
        viewModelScope.launch {
            val session = sessionState.value
            val userId = session.userId
            if (userId == null) {
                // No user; ignore action
                return@launch
            }
            val workOrderId: WorkOrderId? = if (toAdd.isNotEmpty()) {
                when (val ensured = ensureWorkOrderSelectionUseCase(userId, session.workOrderId)) {
                    is EnsureWorkOrderSelectionUseCase.UseCaseResult.AlreadySelected -> ensured.workOrderId
                    is EnsureWorkOrderSelectionUseCase.UseCaseResult.CreatedNew -> {
                        when (val u = updateSelectedWorkOrderIdUseCase(userId, ensured.workOrderId)) {
                            is com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.UpdateSelectedWorkOrderIdUseCase.UseCaseResult.Error -> {
                                return@launch
                            }
                            else -> { /* ok */ }
                        }
                        ensured.workOrderId
                    }
                    is EnsureWorkOrderSelectionUseCase.UseCaseResult.Error -> {
                        return@launch
                    }
                }
            } else {
                session.workOrderId
            }
            if (workOrderId == null) {
                // Nothing to add and no existing work order; only removals requested -> nothing to do
                return@launch
            }
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

    private fun handleConfirmSelectionProceed() {
        val s = viewState.value
        val toAdd = s.pendingAddIdSet
        val toRemove = s.pendingRemoveIdSet
        viewModelScope.launch {
            val session = sessionState.value
            val userId = session.userId
            if (userId == null) {
                return@launch
            }
            val workOrderId: WorkOrderId? = if (toAdd.isNotEmpty()) {
                when (val ensured = ensureWorkOrderSelectionUseCase(userId, session.workOrderId)) {
                    is EnsureWorkOrderSelectionUseCase.UseCaseResult.AlreadySelected -> ensured.workOrderId
                    is EnsureWorkOrderSelectionUseCase.UseCaseResult.CreatedNew -> {
                        when (val u = updateSelectedWorkOrderIdUseCase(userId, ensured.workOrderId)) {
                            is com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.UpdateSelectedWorkOrderIdUseCase.UseCaseResult.Error -> {
                                return@launch
                            }
                            else -> { /* ok */ }
                        }
                        ensured.workOrderId
                    }
                    is EnsureWorkOrderSelectionUseCase.UseCaseResult.Error -> {
                        return@launch
                    }
                }
            } else {
                session.workOrderId
            }
            if (workOrderId == null) {
                return@launch
            }
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
                    isSelectAllChecked = false,
                    isSearchActive = false
                )
            }
        }
    }

    // --- Hoisted search UI handlers ---
    private fun handleTypedSuffixChanged(text: String) {
        setState {
            val combined = buildCombinedQuery(selectedChips, text)
            copy(typedSuffix = text, searchText = combined)
        }
    }

    private fun handleRemoveChip(suggestion: SearchSuggestion) {
        setState {
            val newChips = selectedChips.filterNot { it == suggestion }
            val combined = buildCombinedQuery(newChips, typedSuffix)
            copy(selectedChips = newChips, searchText = combined)
        }
    }

    private fun buildCombinedQuery(chips: List<SearchSuggestion>, typed: String): String {
        val base = chips.joinToString(" ") { it.text }.trim()
        return when {
            base.isNotEmpty() && typed.isNotBlank() -> "$base ${typed.trim()}"
            base.isNotEmpty() -> base
            else -> typed
        }
    }

    private fun WorkOrderId?.handleNull(): WorkOrderId? {
        if (this == null) {
            // setState { copy(error = "No Work Order Selected") }
        }
        return this
    }

}
