package com.gpcasiapac.storesystems.feature.collect.presentation.destination.search

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.flow.QueryFlow
import com.gpcasiapac.storesystems.common.presentation.flow.SearchDebounce
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandler
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandlerDelegate
import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectSessionIds
import com.gpcasiapac.storesystems.feature.collect.domain.model.SearchQuery
import com.gpcasiapac.storesystems.feature.collect.domain.model.SearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.GetCollectSessionIdsFlowUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.search.GetOrderSearchSuggestionListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.search.ObserveSearchOrdersUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.EnsureAndApplyOrderSelectionDeltaUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper.toListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.selection.SelectionCommitResult
import com.gpcasiapac.storesystems.feature.collect.presentation.selection.SelectionHandler
import com.gpcasiapac.storesystems.feature.collect.presentation.selection.SelectionHandlerDelegate
import kotlinx.coroutines.flow.Flow
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
) : MVIViewModel<
        SearchContract.Event,
        SearchContract.State,
        SearchContract.Effect>(),
    SessionHandlerDelegate<CollectSessionIds> by SessionHandler(
        initialSession = CollectSessionIds(),
        sessionFlow = collectSessionIdsFlowUseCase()
    ),
    SelectionHandlerDelegate<InvoiceNumber> by SelectionHandler() {

    override fun setInitialState(): SearchContract.State = SearchContract.State.empty()

    override suspend fun awaitReadiness(): Boolean {
        val collectSessionIds = sessionState.first { it.userId != null }
        return collectSessionIds.userId != null
    }

    override fun handleReadinessFailed() { /* no-op */
    }

    override fun onStart() {
        bindSelectionHandler()

        viewModelScope.launch {
            observeSearchResults()
        }

        viewModelScope.launch {
            observeSearchSuggestions()
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
            is SearchContract.Event.TypedSuffixChanged -> handleTypedSuffixChanged(event.text)
            is SearchContract.Event.RemoveChip -> handleRemoveChip(event.suggestion)

            is SearchContract.Event.Selection -> handleSelection(event.event)
        }
    }


    // Suggestions pipeline: immediate defaults on blank when active, debounced for non-blank
    private suspend fun observeSearchSuggestions() {
        viewState
            .map { it.searchText to it.isSearchActive }
            .flatMapLatest { (text, active) ->
                when {
                    !active -> flowOf(emptyList())
                    else -> {
                        QueryFlow.build(
                            input = flowOf(text),
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

    // Search results pipeline: immediate reset on blank, debounced for non-blank
    private suspend fun observeSearchResults() {

        val queryFlow: Flow<SearchQuery> = QueryFlow.build(
            input = viewState.map { viewState ->
                SearchQuery(viewState.searchText)
            },
            debounce = SearchDebounce(millis = 150),
            keySelector = { query ->
                query.text
            }
        )

        queryFlow.flatMapLatest { query ->
            observeSearchOrdersUseCase(query)
        }.collectLatest { results ->

            setState { copy(searchOrderItems = results.toListItemState()) }
        }


//        viewState
//            .map { it.searchText to it.isSearchActive }
//            .flatMapLatest { (text, active) ->
//                when {
//                    !active -> flowOf(emptyList())
//                    text.isBlank() -> flowOf(emptyList()) // immediate clearing without debounce
//                    else -> {
//                        // Debounce only when non-blank to avoid stale UI on backspace-to-blank
//                        QueryFlow.build(
//                            input = flowOf(text),
//                            debounce = SearchDebounce(millis = 150),
//                            keySelector = { it }
//                        ).flatMapLatest { q -> observeSearchOrdersUseCase(q) }
//                    }
//                }
//            }
//            .map { list -> list.toListItemState() }
//            .collectLatest { results ->
//                setState { copy(searchOrderItems = results) }
//            }
    }


    private fun bindSelectionHandler() {
        // Bind shared selection controller to visible ids and mirror into state
        bindSelection(
            scope = viewModelScope,
            visibleIds = viewState.map { s -> s.searchOrderItems.map { it.invoiceNumber }.toSet() },
            setSelection = { selection ->
                setState { copy(selection = selection) }
            },
            loadPersisted = {
                val workOrderId = sessionState.value.workOrderId
                if (workOrderId == null) emptySet() else observeOrderSelectionUseCase(workOrderId).first()
            },
            commit = { toAdd, toRemove ->
                val session = sessionState.value
                when (val r = ensureAndApplyOrderSelectionDeltaUseCase(
                    userId = session.userId,
                    currentSelectedWorkOrderId = session.workOrderId,
                    toAdd = toAdd,
                    toRemove = toRemove,
                )) {
                    is EnsureAndApplyOrderSelectionDeltaUseCase.UseCaseResult.Error -> SelectionCommitResult.Error(
                        r.message
                    )

                    is EnsureAndApplyOrderSelectionDeltaUseCase.UseCaseResult.Noop -> SelectionCommitResult.Noop
                    is EnsureAndApplyOrderSelectionDeltaUseCase.UseCaseResult.Summary -> SelectionCommitResult.Success
                }
            },
            onRequestConfirmDialog = { setEffect { SearchContract.Effect.ShowMultiSelectConfirmDialog() } },
            onConfirmProceed = null
        )

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

    private fun handleSearchResultClicked(result: InvoiceNumber) {
        setState { copy(searchText = result.value, isSearchActive = false) }
        setEffect { SearchContract.Effect.CollapseSearchBar }
    }

    // ---------------- Selection handling ----------------
    private fun handleConfirmSelection() {
        // Show dialog; actual commit happens based on user choice
        setEffect { SearchContract.Effect.ShowMultiSelectConfirmDialog() }
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
