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
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.EnsureAndApplyOrderSelectionDeltaUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveOrderSelectionUseCase
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
    private val ensureAndApplyOrderSelectionDeltaUseCase: EnsureAndApplyOrderSelectionDeltaUseCase,
    private val collectSessionIdsFlowUseCase: GetCollectSessionIdsFlowUseCase,
    private val toggleOrderSelectionUseCase: com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ToggleOrderSelectionUseCase,
    private val toggleAllOrderSelectionUseCase: com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ToggleAllOrderSelectionUseCase,
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
        val s = viewState.value
        val visibleIds = s.searchOrderItems.map { it.invoiceNumber }.toSet()
        val res = toggleOrderSelectionUseCase(
            orderId = orderId,
            checked = checked,
            persisted = s.existingDraftIdSet,
            pendingAdd = s.pendingAddIdSet,
            pendingRemove = s.pendingRemoveIdSet,
            visibleIds = visibleIds,
        )
        setState {
            copy(
                pendingAddIdSet = res.pendingAdd,
                pendingRemoveIdSet = res.pendingRemove,
                selectedOrderIdList = res.selected,
                isSelectAllChecked = res.isAllSelected,
            )
        }
    }

    private fun handleSelectAll(checked: Boolean) {
        val s = viewState.value
        val visibleIds = s.searchOrderItems.map { it.invoiceNumber }.toSet()
        val res = toggleAllOrderSelectionUseCase(
            checked = checked,
            persisted = s.existingDraftIdSet,
            pendingAdd = s.pendingAddIdSet,
            pendingRemove = s.pendingRemoveIdSet,
            visibleIds = visibleIds,
        )
        setState {
            copy(
                pendingAddIdSet = res.pendingAdd,
                pendingRemoveIdSet = res.pendingRemove,
                selectedOrderIdList = res.selected,
                isSelectAllChecked = res.isAllSelected,
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
            val result = ensureAndApplyOrderSelectionDeltaUseCase(
                userId = session.userId,
                currentSelectedWorkOrderId = session.workOrderId,
                toAdd = toAdd,
                toRemove = toRemove,
            )
            when (result) {
                is EnsureAndApplyOrderSelectionDeltaUseCase.Result.Error -> {
                    // Optionally surface error; for now just ignore
                    return@launch
                }
                else -> Unit
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
            val result = ensureAndApplyOrderSelectionDeltaUseCase(
                userId = session.userId,
                currentSelectedWorkOrderId = session.workOrderId,
                toAdd = toAdd,
                toRemove = toRemove,
            )
            when (result) {
                is EnsureAndApplyOrderSelectionDeltaUseCase.Result.Error -> {
                    return@launch
                }
                else -> Unit
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
