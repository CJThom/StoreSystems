package com.gpcasiapac.storesystems.feature.collect.presentation.search

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.flow.QueryFlow
import com.gpcasiapac.storesystems.common.presentation.flow.SearchDebounce
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.GetOrderSearchSuggestionListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveSearchOrdersUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper.toListItemState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SearchViewModel(
    private val observeSearchOrdersUseCase: ObserveSearchOrdersUseCase,
    private val getOrderSearchSuggestionListUseCase: GetOrderSearchSuggestionListUseCase,
    // Selection persistence dependencies
    private val observeOrderSelectionUseCase: com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.ObserveOrderSelectionUseCase,
    private val setOrderSelectionUseCase: com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.SetOrderSelectionUseCase,
    private val addOrderSelectionUseCase: com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.AddOrderSelectionUseCase,
    private val removeOrderSelectionUseCase: com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.RemoveOrderSelectionUseCase,
    private val clearOrderSelectionUseCase: com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.ClearOrderSelectionUseCase,
) : MVIViewModel<SearchContract.Event, SearchContract.State, SearchContract.Effect>() {

    private val userRefId = "mock"

    override fun setInitialState(): SearchContract.State = SearchContract.State.empty()

    override suspend fun awaitReadiness(): Boolean = true

    override fun handleReadinessFailed() { /* no-op */ }

    override fun onStart() {
        // Search results pipeline: debounced and independent
        viewModelScope.launch {
            QueryFlow.build(
                input = viewState.map { it.searchText to it.isSearchActive },
                debounce = SearchDebounce(millis = 150),
                keySelector = { (text, active) -> if (active) text.trim() else "" }
            ).flatMapLatest { (text, active) ->
                val t = text.trim()
                if (!active || t.isEmpty()) flowOf(emptyList()) else observeSearchOrdersUseCase(t)
            }.map { list -> list.toListItemState() }
             .collectLatest { results ->
                 setState { copy(searchResults = results) }
             }
        }

        // Suggestions pipeline
        viewModelScope.launch {
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
    }

    override fun handleEvents(event: SearchContract.Event) {
        when (event) {
            is SearchContract.Event.SearchTextChanged -> handleSearchTextChanged(event.text)
            is SearchContract.Event.SearchOnExpandedChange -> handleSearchOnExpandedChange(event.expand)
            SearchContract.Event.ClearSearch -> handleClearSearch()
            SearchContract.Event.SearchBarBackPressed -> handleSearchOnExpandedChange(false)
            is SearchContract.Event.SearchResultClicked -> handleSearchResultClicked(event.result)
            is SearchContract.Event.SearchSuggestionClicked -> handleSearchSuggestionClicked(event.suggestion)

            // Selection events
            is SearchContract.Event.ToggleSelectionMode -> handleToggleSelectionMode(event.enabled)
            is SearchContract.Event.OrderChecked -> handleOrderChecked(event.orderId, event.checked)
            is SearchContract.Event.SelectAll -> handleSelectAll(event.checked)
            SearchContract.Event.CancelSelection -> handleCancelSelection()
            SearchContract.Event.ConfirmSelection -> handleConfirmSelection()
            SearchContract.Event.ConfirmSelectionStay -> handleConfirmSelectionStay()
            SearchContract.Event.ConfirmSelectionProceed -> handleConfirmSelectionProceed()
            SearchContract.Event.DismissConfirmSelectionDialog -> { /* no-op for now */ }
        }
    }

    private fun handleSearchTextChanged(text: String) {
        setState { copy(searchText = text) }
    }

    private fun handleSearchOnExpandedChange(expand: Boolean) {
        setState { copy(isSearchActive = expand) }
        setEffect { if (expand) SearchContract.Effect.ExpandSearchBar else SearchContract.Effect.CollapseSearchBar }
    }

    private fun handleClearSearch() {
        setState { copy(searchText = "", searchResults = emptyList(), orderSearchSuggestionList = emptyList()) }
    }

    private fun handleSearchSuggestionClicked(suggestion: String) {
        setState { copy(searchText = suggestion) }
    }

    private fun handleSearchResultClicked(result: String) {
        setState { copy(searchText = result, isSearchActive = false) }
        setEffect { SearchContract.Effect.CollapseSearchBar }
    }

    // ---------------- Selection handling ----------------
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
            val visibleIds = searchResults.map { it.invoiceNumber }.toSet()
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
        val visibleIds = viewState.value.searchResults.map { it.invoiceNumber }.toSet()
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
            // Commit adds and removals
            toAdd.forEach { addOrderSelectionUseCase(it, userRefId) }
            toRemove.forEach { removeOrderSelectionUseCase(it, userRefId) }
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
                    isSearchActive = false
                )
            }
        }
    }
}
