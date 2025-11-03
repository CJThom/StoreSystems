package com.gpcasiapac.storesystems.feature.history.presentation.destination.historydetails

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.feature.history.api.HistoryType
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem

object HistoryDetailsScreenContract {

    @Immutable
    data class State(
        val type: HistoryType? = null,
        val id: String = "",
        val isLoading: Boolean = true,
        val error: String? = null,
        val item: HistoryItem? = null
    ) : ViewState

    sealed interface Event : ViewEvent {
        data class Initialize(val type: HistoryType, val id: String) : Event
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
