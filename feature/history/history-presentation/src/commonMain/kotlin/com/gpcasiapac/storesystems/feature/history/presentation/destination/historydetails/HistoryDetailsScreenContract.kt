package com.gpcasiapac.storesystems.feature.history.presentation.destination.historydetails

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem

object HistoryDetailsScreenContract {

    @Immutable
    data class State(
        val title: String = "",
        val groupKey: String = "",
        val isLoading: Boolean = true,
        val error: String? = null,
        val items: List<HistoryItem> = emptyList()
    ) : ViewState

    sealed interface Event : ViewEvent {
        data class Initialize(val title: String, val groupKey: String) : Event
        data object Refresh : Event
        data object Back : Event
    }

    sealed interface Effect : ViewSideEffect {
        data class ShowError(val message: String) : Effect
        sealed interface Outcome : Effect {
            data object Back : Outcome
        }
    }
}
