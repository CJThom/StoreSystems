package com.gpcasiapac.storesystems.feature.collect.presentation.destination.search

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestionType
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState

object SearchContract {

    @Immutable
    data class State(
        val searchText: String,
        val isSearchActive: Boolean,
        val orderSearchSuggestionList: List<OrderSearchSuggestion>,
        val searchResults: List<CollectOrderListItemState>,
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
                orderSearchSuggestionList = emptyList(),
                searchResults = emptyList(),
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
        data class SearchSuggestionClicked(val suggestion: String, val type: OrderSearchSuggestionType) : Event

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
