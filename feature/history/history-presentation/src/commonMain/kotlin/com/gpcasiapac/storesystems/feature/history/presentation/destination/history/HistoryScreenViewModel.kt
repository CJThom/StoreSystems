package com.gpcasiapac.storesystems.feature.history.presentation.destination.history

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HistoryScreenViewModel : MVIViewModel<HistoryScreenContract.Event, HistoryScreenContract.State, HistoryScreenContract.Effect>() {

    override fun setInitialState(): HistoryScreenContract.State = HistoryScreenContract.State(
        items = emptyList(),
        isLoading = true,
        error = null,
    )

    override suspend fun awaitReadiness(): Boolean {
        // Placeholder screen; no readiness gating
        return true
    }

    override fun handleReadinessFailed() { /* no-op */ }

    override fun onStart() {
        // Initial load when view state is first collected per MVIViewModel contract
        viewModelScope.launch {
            loadItems(
                onSuccess = { setEffect { HistoryScreenContract.Effect.ShowToast("History loaded") } },
                onError = { message -> setEffect { HistoryScreenContract.Effect.ShowError(message) } }
            )
        }
    }

    // TABLE OF CONTENTS - Events handler
    override fun handleEvents(event: HistoryScreenContract.Event) {
        when (event) {
            is HistoryScreenContract.Event.Load -> viewModelScope.launch {
                loadItems(
                    onSuccess = { setEffect { HistoryScreenContract.Effect.ShowToast("History loaded") } },
                    onError = { message -> setEffect { HistoryScreenContract.Effect.ShowError(message) } }
                )
            }
            is HistoryScreenContract.Event.Refresh -> viewModelScope.launch {
                loadItems(
                    onSuccess = { setEffect { HistoryScreenContract.Effect.ShowToast("History refreshed") } },
                    onError = { message -> setEffect { HistoryScreenContract.Effect.ShowError(message) } }
                )
            }
            is HistoryScreenContract.Event.OpenItem -> { /* Placeholder: could navigate to details later */ }
            is HistoryScreenContract.Event.ClearError -> clearError()
            is HistoryScreenContract.Event.Back -> setEffect { HistoryScreenContract.Effect.Outcome.Back }
        }
    }

    private suspend fun loadItems(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        setState { copy(isLoading = true, error = null) }
        try {
            delay(400)
            val demo = (1..12).map { idx -> "History Item #$idx" }
            setState { copy(items = demo, isLoading = false, error = null) }
            onSuccess()
        } catch (t: Throwable) {
            val message = t.message ?: "Failed to load history. Please try again."
            setState { copy(isLoading = false, error = message) }
            onError(message)
        }
    }

    private fun clearError() { setState { copy(error = null) } }
}