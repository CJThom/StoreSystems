package com.gpcasiapac.storesystems.feature.collect.presentation.destination.search

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.common.presentation.flow.QueryFlow
import com.gpcasiapac.storesystems.common.presentation.flow.SearchDebounce
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandler
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandlerDelegate
import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectSessionIds
import com.gpcasiapac.storesystems.feature.collect.domain.model.SearchQuery
import com.gpcasiapac.storesystems.feature.collect.domain.model.SearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.SuggestionQuery
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchViewModel(
    private val logger: Logger,
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

    val log = logger.withTag("SearchViewModel")

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
            is SearchContract.Event.OnQueryChanged -> handleQueryChanged(event.query)
            is SearchContract.Event.OnExpandedChanged -> handleSearchOnExpandedChange(event.expand)
            is SearchContract.Event.ClearSearch -> handleClearSearch()
            is SearchContract.Event.SearchBarBackPressed -> handleCollapseSearchBar()
            is SearchContract.Event.SearchResultClicked -> handleSearchResultClicked(event.result)
            is SearchContract.Event.SearchSuggestionClicked -> handleSearchSuggestionClicked(event.suggestion)
            is SearchContract.Event.RemoveChip -> handleRemoveSuggestion(event.suggestion)
            is SearchContract.Event.Selection -> handleSelection(event.event)
            is SearchContract.Event.ExpandSearchBar -> handleExpandSearchBar()
            is SearchContract.Event.CollapseSearchBar -> handleCollapseSearchBar()
            is SearchContract.Event.OnSearchClicked -> handleSearchClicked()

        }
    }

    private fun handleQueryChanged(query: String) {
        setState { copy(query = query) }
    }

    // Suggestions pipeline: immediate defaults on blank when active, debounced for non-blank
    private suspend fun observeSearchSuggestions() {
        // val textFlow = snapshotFlow { viewState.value.query.text.toString() }

        val queryFlow: Flow<SuggestionQuery> = QueryFlow.build(
            input = viewState.map { state ->
                SuggestionQuery(
                    text = state.query,
                    selected = state.selectedSuggestionList
                )
            },
            debounce = SearchDebounce(millis = 150),
            keySelector = { query ->
                // Include selected chips in key so chip changes trigger a refresh even with blank text
                query.text + "|" + query.selected.joinToString("|") { it.kind.name + ":" + it.text }
            }
        )

        queryFlow.flatMapLatest { query ->
            flowOf(getOrderSearchSuggestionListUseCase(query))
        }.collectLatest { suggestions ->
            setState { copy(searchSuggestionList = suggestions) }
        }

    }

    // Search results pipeline: immediate reset on blank, debounced for non-blank
    private suspend fun observeSearchResults() {

        //  val textFlow = snapshotFlow { viewState.value.query.text.toString() }

        val queryFlow: Flow<SearchQuery> = QueryFlow.build(
            input = viewState.map { state ->
                SearchQuery(
                    text = state.query,
                    selected = state.selectedSuggestionList
                )
            },
            debounce = SearchDebounce(millis = 150),
            keySelector = { query ->
                // Include selected chips in key so chip changes trigger results refresh even with blank text
                query.text + "|" + query.selected.joinToString("|") { it.kind.name + ":" + it.text }
            }
        )

        queryFlow.flatMapLatest { query ->
            observeSearchOrdersUseCase(query)
        }.collectLatest { results ->
            setState { copy(searchOrderItemList = results.toListItemState()) }
        }

    }

    private fun bindSelectionHandler() {
        // Bind shared selection controller to visible ids and mirror into state
        bindSelection(
            scope = viewModelScope,
            visibleIds = viewState.map { s ->
                s.searchOrderItemList.map { it.invoiceNumber }.toSet()
            },
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

    private fun handleSearchClicked() {


    }

    private fun handleSearchOnExpandedChange(expand: Boolean) {
        setState { copy(isSearchActive = expand) }
    }

    private fun handleExpandSearchBar() {
        setEffect { SearchContract.Effect.ExpandSearchBar }
    }

    private fun handleCollapseSearchBar() {
        setState { copy(selectedSuggestionList = emptyList()) }
        setEffect { SearchContract.Effect.ClearQueryField }
        setEffect { SearchContract.Effect.CollapseSearchBar }
    }

    private fun handleClearSearch() {
        setState {
            copy(

                //  query = query.apply { setTextAndPlaceCursorAtEnd("") },
                selectedSuggestionList = emptyList(),
            )
        }
        setEffect { SearchContract.Effect.ClearQueryField }
    }

    private fun handleSearchSuggestionClicked(suggestion: SearchSuggestion) {
        viewModelScope.launch {
            setState {
                val exists = selectedSuggestionList.any { it == suggestion }
                val newChips = if (exists) selectedSuggestionList else selectedSuggestionList + suggestion
                copy(selectedSuggestionList = newChips)
            }
            // Clear query field after selecting a suggestion
            setEffect { SearchContract.Effect.ClearQueryField }
            // Compose-only concern will auto-scroll the chips into view (Option A)
        }
    }

    private fun handleSearchResultClicked(result: InvoiceNumber) {
        setEffect { SearchContract.Effect.SetQueryField(result.value) }
    }

    // ---------------- Selection handling ----------------
    private fun handleConfirmSelection() {
        // Show dialog; actual commit happens based on user choice
        setEffect { SearchContract.Effect.ShowMultiSelectConfirmDialog() }
    }


    private fun handleRemoveSuggestion(suggestion: SearchSuggestion) {
        setState {
            val newChips = selectedSuggestionList.filterNot { it == suggestion }
            copy(selectedSuggestionList = newChips)
        }
    }

    private fun buildCombinedQuery(suggestionList: List<SearchSuggestion>, typed: String): String {
        val base = suggestionList.joinToString(" ") { it.text }.trim()
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
