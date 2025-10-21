package com.gpcasiapac.storesystems.feature.collect.presentation.destination.search

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.feature.collect.domain.model.SearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState

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
        // Multi-select state for search context
        val isMultiSelectionEnabled: Boolean,
        val selectedOrderIdList: Set<String>,
        val isSelectAllChecked: Boolean,
        // Normalized selection-editing model (independent from OrderList)
        val existingDraftIdSet: Set<String>,
        val pendingAddIdSet: Set<String>,
        val pendingRemoveIdSet: Set<String>,
    ) : ViewState {
        companion object {
            fun empty(): State = State(
                searchText = "",
                isSearchActive = false,
                searchSuggestions = emptyList(),
                searchOrderItems = emptyList(),
                selectedChips = emptyList(),
                typedSuffix = "",
                isMultiSelectionEnabled = false,
                selectedOrderIdList = emptySet(),
                isSelectAllChecked = false,
                existingDraftIdSet = emptySet(),
                pendingAddIdSet = emptySet(),
                pendingRemoveIdSet = emptySet(),
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

        // Selection mode & actions (search context)
        data class ToggleSelectionMode(val enabled: Boolean) : Event
        data class OrderChecked(val orderId: String, val checked: Boolean) : Event
        data class SelectAll(val checked: Boolean) : Event
        data object CancelSelection : Event
        data object ConfirmSelection : Event
        // Confirmation dialog actions for search context
        data object ConfirmSelectionStay : Event
        data object ConfirmSelectionProceed : Event
        data object DismissConfirmSelectionDialog : Event
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
