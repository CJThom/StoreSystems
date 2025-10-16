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
import kotlinx.coroutines.launch

class SearchViewModel(
    private val observeSearchOrdersUseCase: ObserveSearchOrdersUseCase,
    private val getOrderSearchSuggestionListUseCase: GetOrderSearchSuggestionListUseCase,
) : MVIViewModel<SearchContract.Event, SearchContract.State, SearchContract.Effect>() {

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
}
