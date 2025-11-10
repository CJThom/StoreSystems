package com.gpcasiapac.storesystems.feature.history.presentation.destination.historydetails

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.history.api.HistoryType
import com.gpcasiapac.storesystems.feature.history.domain.usecase.ObserveCollectHistoryItemByIdUseCase
import com.gpcasiapac.storesystems.feature.history.domain.usecase.ObserveHistoryItemByIdUseCase
import com.gpcasiapac.storesystems.feature.history.domain.usecase.RetryHistoryUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class HistoryDetailsScreenViewModel(
    private val observeHistoryItemById: ObserveHistoryItemByIdUseCase,
    private val retryHistoryUseCase: RetryHistoryUseCase,
) : MVIViewModel<HistoryDetailsScreenContract.Event, HistoryDetailsScreenContract.State, HistoryDetailsScreenContract.Effect>() {

    private var observeJob: Job? = null

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

            is HistoryDetailsScreenContract.Event.Refresh -> startObserving(
                type = viewState.value.type ?: HistoryType.UNKNOWN,
                id = viewState.value.id
            )
            is HistoryDetailsScreenContract.Event.Back -> setEffect { HistoryDetailsScreenContract.Effect.Outcome.Back }
            HistoryDetailsScreenContract.Event.Retry -> handleRetryItem()
        }
    }

    private fun handleRetryItem() {
        viewModelScope.launch {
            retryHistoryUseCase(viewState.value.id)
                .onSuccess { /* flow may emit updated item if source changes */ }
                .onFailure { error ->
                    val message = error.message ?: "Failed to retry"
                    setEffect { HistoryDetailsScreenContract.Effect.ShowError(message) }
                }
        }
    }

    private fun initialize(type: HistoryType, id: String) {
        setState { copy(type = type, id = id) }
        startObserving(type = type, id = id)
    }

    private fun startObserving(type: HistoryType, id: String) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            observeHistoryItemById(type = type, id = id)
                .onStart { setState { copy(isLoading = true, error = null) } }
                .catch { e ->
                    val msg = e.message ?: "Failed to load details"
                    setState { copy(isLoading = false, error = msg) }
                    setEffect { HistoryDetailsScreenContract.Effect.ShowError(msg) }
                }
                .collect { item ->
                    setState { copy(item = item, isLoading = false) }
                }
        }
    }
}
