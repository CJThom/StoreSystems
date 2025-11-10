package com.gpcasiapac.storesystems.feature.collect.presentation.destination.search

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.domain.model.SearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.selection.SelectionContract
import com.gpcasiapac.storesystems.feature.collect.presentation.selection.SelectionUiState

object SearchContract {

    @Immutable
    data class State(
        val query: String,
        val isSearchActive: Boolean,
        val searchSuggestionList: List<SearchSuggestion>,
        val searchOrderItemList: List<CollectOrderListItemState>,
        // Hoisted search UI state
        val selectedSuggestionList: List<SearchSuggestion>,
        // Shared selection slice for search context
        val selection: SelectionUiState<InvoiceNumber> = SelectionUiState(),
    ) : ViewState {
        companion object {

            fun empty(): State = State(
                query = "",
                isSearchActive = false,
                searchSuggestionList = emptyList(),
                searchOrderItemList = emptyList(),
                selectedSuggestionList = emptyList(),
                selection = SelectionUiState(),
            )
        }
    }

    sealed interface Event : ViewEvent {
        data class OnExpandedChanged(val expand: Boolean) : Event
        data class OnQueryChanged(val query: String) : Event

        data object OnSearchClicked : Event
        data object ExpandSearchBar : Event
        data object CollapseSearchBar : Event
        data object ClearSearch : Event
        data object SearchBarBackPressed : Event
        data class SearchResultClicked(val result: InvoiceNumber) : Event
        data class SearchSuggestionClicked(val suggestion: SearchSuggestion) : Event

        // Hoisted search UI interactions
        data class RemoveChip(val suggestion: SearchSuggestion) : Event

        data object OnAcceptMultiSelectClicked : Event

        // Shared selection wrapper (replaces per-screen selection events)
        data class Selection(val event: SelectionContract.Event<InvoiceNumber>) : Event
    }

    sealed interface Effect : ViewSideEffect {
        data object ExpandSearchBar : Effect
        data object CollapseSearchBar : Effect
        data object ClearQueryField : Effect
        data class SetQueryField(val text: String, val moveCursorToEnd: Boolean = true) : Effect
        data object FocusQueryField : Effect

        sealed interface Outcome : Effect {
            data class OrderClicked(val invoiceNumber: InvoiceNumber) : Outcome
            data object RequestConfirmationDialog : Outcome
            data object Back : Outcome
        }

        // Multi-select confirmation dialog trigger for search
//        data class ShowMultiSelectConfirmDialog(
//            val title: String = "Confirm selection",
//            val cancelLabel: String = "Cancel",
//            val selectOnlyLabel: String = "Select only",
//            val proceedLabel: String = "Select and proceed",
//        ) : Effect
    }
}
