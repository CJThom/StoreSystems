package com.gpcasiapac.storesystems.feature.collect.presentation.search

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
    ) : ViewState {
        companion object {
            fun empty(): State = State(
                searchText = "",
                isSearchActive = false,
                orderSearchSuggestionList = emptyList(),
                searchResults = emptyList(),
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
    }

    sealed interface Effect : ViewSideEffect {
        data object ExpandSearchBar : Effect
        data object CollapseSearchBar : Effect
    }
}
