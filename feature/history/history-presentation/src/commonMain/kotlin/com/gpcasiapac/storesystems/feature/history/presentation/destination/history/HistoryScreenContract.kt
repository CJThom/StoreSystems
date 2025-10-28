package com.gpcasiapac.storesystems.feature.history.presentation.destination.history

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryFilter
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItemWithMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryType

object HistoryScreenContract {

    @Immutable
    data class State(
        val items: List<HistoryItemWithMetadata>,  // Changed: Now uses domain model directly
        val isLoading: Boolean,
        val error: String?,
        val filter: HistoryFilter = HistoryFilter.ALL,
        val typeFilter: HistoryType? = null,
        val searchQuery: String = ""
    ) : ViewState

    sealed interface Event : ViewEvent {
        data object Load : Event
        data object Refresh : Event
        data class OpenItem(val id: String) : Event
        data class DeleteItem(val id: String) : Event
        data class RetryItem(val id: String) : Event
        data class FilterChanged(val filter: HistoryFilter) : Event
        data class TypeFilterChanged(val type: HistoryType?) : Event
        data class SearchQueryChanged(val query: String) : Event
        data object ClearError : Event
        data object Back : Event
    }

    sealed interface Effect : ViewSideEffect {
        data class ShowToast(val message: String) : Effect
        data class ShowError(val error: String) : Effect

        sealed interface Outcome : Effect {
            data object Back : Outcome
        }
    }
}
