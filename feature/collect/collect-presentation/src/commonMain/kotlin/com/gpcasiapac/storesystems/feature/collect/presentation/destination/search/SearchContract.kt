package com.gpcasiapac.storesystems.feature.collect.presentation.destination.search

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.feature.collect.domain.model.SearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.selection.SelectionContract
import com.gpcasiapac.storesystems.feature.collect.presentation.selection.SelectionUiState

object SearchContract {

    @Immutable
    data class State(
        val searchText: String,
        val isSearchActive: Boolean,
        val searchSuggestions: List<SearchSuggestion>,
        val searchOrderItems: List<CollectOrderListItemState>,
        // Hoisted search UI state
        val selectedChips: List<SearchSuggestion>,
        val typedSuffix: String,
        // Shared selection slice for search context
        val selection: SelectionUiState = SelectionUiState(),
    ) : ViewState {
        companion object {
            fun empty(): State = State(
                searchText = "",
                isSearchActive = false,
                searchSuggestions = emptyList(),
                searchOrderItems = emptyList(),
                selectedChips = emptyList(),
                typedSuffix = "",
                selection = SelectionUiState(),
            )
        }
    }

    sealed interface Event : ViewEvent {
        data class SearchTextChanged(val text: String) : Event
        data class SearchOnExpandedChange(val expand: Boolean) : Event
        data object ClearSearch : Event
        data object SearchBarBackPressed : Event
        data class SearchResultClicked(val result: String) : Event
        data class SearchSuggestionClicked(val suggestion: SearchSuggestion) : Event
        // Hoisted search UI interactions
        data class TypedSuffixChanged(val text: String) : Event
        data class RemoveChip(val suggestion: SearchSuggestion) : Event

        // Shared selection wrapper (replaces per-screen selection events)
        data class Selection(val event: SelectionContract.Event) : Event
    }

    sealed interface Effect : ViewSideEffect {
        data object ExpandSearchBar : Effect
        data object CollapseSearchBar : Effect
        // Multi-select confirmation dialog trigger for search
        data class ShowMultiSelectConfirmDialog(
            val title: String = "Confirm selection",
            val cancelLabel: String = "Cancel",
            val selectOnlyLabel: String = "Select only",
            val proceedLabel: String = "Select and proceed",
        ) : Effect
    }
}
