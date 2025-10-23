package com.gpcasiapac.storesystems.feature.history.presentation.destination.history

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryFilter
import com.gpcasiapac.storesystems.feature.history.domain.usecase.DeleteHistoryItemUseCase
import com.gpcasiapac.storesystems.feature.history.domain.usecase.GetHistoryUseCase
import com.gpcasiapac.storesystems.feature.history.domain.usecase.RetryHistoryItemUseCase
import com.gpcasiapac.storesystems.feature.history.presentation.mapper.toUi
import kotlinx.coroutines.launch

class HistoryScreenViewModel(
    private val getHistoryUseCase: GetHistoryUseCase,
    private val deleteHistoryItemUseCase: DeleteHistoryItemUseCase,
    private val retryHistoryItemUseCase: RetryHistoryItemUseCase
) : MVIViewModel<HistoryScreenContract.Event, HistoryScreenContract.State, HistoryScreenContract.Effect>() {

    override fun setInitialState(): HistoryScreenContract.State = HistoryScreenContract.State(
        items = emptyList(),
        isLoading = true,
        error = null,
        filter = HistoryFilter.ALL
    )

    override suspend fun awaitReadiness(): Boolean {
        // No special readiness check needed for v1
        return true
    }

    override fun handleReadinessFailed() { /* no-op */ }

    override fun onStart() {
        // Start observing history when view state is first collected
        observeHistory()
    }

    override fun handleEvents(event: HistoryScreenContract.Event) {
        when (event) {
            is HistoryScreenContract.Event.Load -> observeHistory()
            is HistoryScreenContract.Event.Refresh -> observeHistory()
            is HistoryScreenContract.Event.OpenItem -> handleOpenItem(event.id)
            is HistoryScreenContract.Event.DeleteItem -> handleDeleteItem(event.id)
            is HistoryScreenContract.Event.RetryItem -> handleRetryItem(event.id)
            is HistoryScreenContract.Event.FilterChanged -> handleFilterChanged(event.filter)
            is HistoryScreenContract.Event.ClearError -> clearError()
            is HistoryScreenContract.Event.Back -> setEffect { HistoryScreenContract.Effect.Outcome.Back }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            
            try {
                getHistoryUseCase().collect { historyItems ->
                    val uiItems = historyItems.toUi()
                    val filteredItems = applyFilter(uiItems, viewState.value.filter)
                    
                    setState {
                        copy(
                            items = filteredItems,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (t: Throwable) {
                val message = t.message ?: "Failed to load history"
                setState { copy(isLoading = false, error = message) }
                setEffect { HistoryScreenContract.Effect.ShowError(message) }
            }
        }
    }

    private fun handleOpenItem(id: String) {
        // TODO: Navigate to detail screen in future version
        setEffect { HistoryScreenContract.Effect.ShowToast("Detail view coming soon") }
    }

    private fun handleDeleteItem(id: String) {
        viewModelScope.launch {
            deleteHistoryItemUseCase(id)
                .onSuccess {
                    setEffect { HistoryScreenContract.Effect.ShowToast("Item deleted") }
                }
                .onFailure { error ->
                    val message = error.message ?: "Failed to delete item"
                    setEffect { HistoryScreenContract.Effect.ShowError(message) }
                }
        }
    }

    private fun handleRetryItem(id: String) {
        viewModelScope.launch {
            retryHistoryItemUseCase(id)
                .onSuccess {
                    setEffect { HistoryScreenContract.Effect.ShowToast("Retrying task...") }
                }
                .onFailure { error ->
                    val message = error.message ?: "Failed to retry item"
                    setEffect { HistoryScreenContract.Effect.ShowError(message) }
                }
        }
    }

    private fun handleFilterChanged(filter: HistoryFilter) {
        val filteredItems = applyFilter(viewState.value.items, filter)
        setState { copy(filter = filter, items = filteredItems) }
    }

    private fun applyFilter(
        items: List<com.gpcasiapac.storesystems.feature.history.presentation.model.HistoryItemUi>,
        filter: HistoryFilter
    ): List<com.gpcasiapac.storesystems.feature.history.presentation.model.HistoryItemUi> {
        return when (filter) {
            HistoryFilter.ALL -> items
            HistoryFilter.PENDING -> items.filter { 
                it.status == com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus.PENDING 
            }
            HistoryFilter.FAILED -> items.filter { 
                it.status == com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus.FAILED 
            }
            HistoryFilter.COMPLETED -> items.filter { 
                it.status == com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus.COMPLETED 
            }
        }
    }

    private fun clearError() {
        setState { copy(error = null) }
    }
}