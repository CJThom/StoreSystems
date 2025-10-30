package com.gpcasiapac.storesystems.feature.history.presentation.destination.historydetails

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.history.domain.usecase.GetHistoryUseCase
import kotlinx.coroutines.launch

class HistoryDetailsScreenViewModel(
    private val getHistoryUseCase: GetHistoryUseCase,
) : MVIViewModel<HistoryDetailsScreenContract.Event, HistoryDetailsScreenContract.State, HistoryDetailsScreenContract.Effect>() {

    override fun setInitialState(): HistoryDetailsScreenContract.State =
        HistoryDetailsScreenContract.State()

    override suspend fun awaitReadiness(): Boolean = true

    override fun handleReadinessFailed() {}

    override fun onStart() {}

    override fun handleEvents(event: HistoryDetailsScreenContract.Event) {
        when (event) {
            is HistoryDetailsScreenContract.Event.Initialize -> initialize(
                event.title,
                event.groupKey
            )

            is HistoryDetailsScreenContract.Event.Refresh -> load()
            is HistoryDetailsScreenContract.Event.Back -> setEffect { HistoryDetailsScreenContract.Effect.Outcome.Back }
        }
    }

    private fun initialize(title: String, key: String) {
        val current = viewState.value
        if (current.title == title && current.groupKey == key && current.items.isNotEmpty()) return
        setState { copy(title = title, groupKey = key) }
        load()
    }

    private fun load() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            try {
                val key = viewState.value.groupKey
                viewModelScope.launch {
                    getHistoryUseCase().collect {
                        setState { copy(items = it, isLoading = false) }
                    }
                }

            } catch (t: Throwable) {
                val msg = t.message ?: "Failed to load details"
                setState { copy(isLoading = false, error = msg) }
                setEffect { HistoryDetailsScreenContract.Effect.ShowError(msg) }
            }
        }
    }
}
