package com.gpcasiapac.storesystems.feature.history.presentation.destination.history

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryFilter
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryType
import com.gpcasiapac.storesystems.feature.history.domain.usecase.DeleteHistoryItemUseCase
import com.gpcasiapac.storesystems.feature.history.domain.usecase.GetHistoryUseCase
import com.gpcasiapac.storesystems.feature.history.domain.usecase.RetryHistoryItemUseCase
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
        filter = HistoryFilter.ALL,
        typeFilter = null,
        searchQuery = ""
    )

    override suspend fun awaitReadiness(): Boolean {
        return true
    }

    override fun handleReadinessFailed() { /* no-op */ }

    override fun onStart() {
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
            is HistoryScreenContract.Event.TypeFilterChanged -> handleTypeFilterChanged(event.type)
            is HistoryScreenContract.Event.SearchQueryChanged -> handleSearchQueryChanged(event.query)
            is HistoryScreenContract.Event.ClearError -> clearError()
            is HistoryScreenContract.Event.Back -> setEffect { HistoryScreenContract.Effect.Outcome.Back }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            
            try {
                val state = viewState.value
                getHistoryUseCase.invoke(
                    filter = state.filter,
                    typeFilter = state.typeFilter,
                    searchQuery = state.searchQuery.takeIf { it.isNotBlank() }
                ).collect { items ->
                    setState {
                        copy(
                            items = items,
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
        println("Processing event...")
        val title = "Invoice #$id"
        setEffect { HistoryScreenContract.Effect.Outcome.OpenDetails(title = title, groupKey = id) }
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
        setState { copy(filter = filter) }
        // Filters are applied in the use case via Flow transformation
    }
    
    private fun handleTypeFilterChanged(type: HistoryType?) {
        setState { copy(typeFilter = type) }
    }
    
    private fun handleSearchQueryChanged(query: String) {
        setState { copy(searchQuery = query) }
    }

    private fun clearError() {
        setState { copy(error = null) }
    }
}