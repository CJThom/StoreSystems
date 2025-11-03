package com.gpcasiapac.storesystems.feature.history.presentation.destination.historydetails

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.history.api.HistoryType
import kotlinx.coroutines.launch

class HistoryDetailsScreenViewModel(
    private val getHistoryItemById: com.gpcasiapac.storesystems.feature.history.domain.usecase.GetHistoryItemByIdUseCase,
) : MVIViewModel<HistoryDetailsScreenContract.Event, HistoryDetailsScreenContract.State, HistoryDetailsScreenContract.Effect>() {

    override fun setInitialState(): HistoryDetailsScreenContract.State =
        HistoryDetailsScreenContract.State()

    override suspend fun awaitReadiness(): Boolean = true

    override fun handleReadinessFailed() {}

    override fun onStart() {}

    override fun handleEvents(event: HistoryDetailsScreenContract.Event) {
        when (event) {
            is HistoryDetailsScreenContract.Event.Initialize -> initialize(
                event.type,
                event.id
            )

            is HistoryDetailsScreenContract.Event.Refresh -> load()
            is HistoryDetailsScreenContract.Event.Back -> setEffect { HistoryDetailsScreenContract.Effect.Outcome.Back }
        }
    }

    private fun initialize(type: HistoryType, id: String) {
        val current = viewState.value
        if (current.type == type && current.id == id && current.item != null) return
        setState { copy(type = type, id = id) }
        load()
    }

    private fun load() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            try {
                val id = viewState.value.id
                val type = viewState.value.type
                val result = getHistoryItemById(type ?: HistoryType.UNKNOWN, id)
                result.onSuccess { item ->
                    if (item != null) {
                        setState { copy(item = item, isLoading = false) }
                    } else {
                        val msg = "History item not found"
                        setState { copy(isLoading = false, error = msg) }
                        setEffect { HistoryDetailsScreenContract.Effect.ShowError(msg) }
                    }
                }.onFailure { e ->
                    val msg = e.message ?: "Failed to load details"
                    setState { copy(isLoading = false, error = msg) }
                    setEffect { HistoryDetailsScreenContract.Effect.ShowError(msg) }
                }
            } catch (t: Throwable) {
                val msg = t.message ?: "Failed to load details"
                setState { copy(isLoading = false, error = msg) }
                setEffect { HistoryDetailsScreenContract.Effect.ShowError(msg) }
            }
        }
    }
}
