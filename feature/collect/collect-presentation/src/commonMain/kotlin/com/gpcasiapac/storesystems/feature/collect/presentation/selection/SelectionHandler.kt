package com.gpcasiapac.storesystems.feature.collect.presentation.selection

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SelectionHandler : SelectionHandlerDelegate {

    private val _state = MutableStateFlow(SelectionUiState())
    override val state: StateFlow<SelectionUiState> = _state

    private var scope: CoroutineScope? = null
    private var lastVisible: Set<String> = emptySet()
    private var loadPersisted: (suspend () -> Set<String>)? = null
    private var commit: (suspend (Set<String>, Set<String>) -> SelectionCommitResult)? = null

    override fun bindSelection(
        scope: CoroutineScope,
        visibleIds: Flow<Set<String>>,
        setSelection: (SelectionUiState) -> Unit,
        loadPersisted: suspend () -> Set<String>,
        commit: suspend (toAdd: Set<String>, toRemove: Set<String>) -> SelectionCommitResult,
    ) {
        this.scope = scope
        this.loadPersisted = loadPersisted
        this.commit = commit

        scope.launch { visibleIds.collectLatest { onVisibleIdsChanged(it) } }
        scope.launch { state.collectLatest { sel -> setSelection(sel) } }
    }

    private fun onVisibleIdsChanged(visible: Set<String>) {
        lastVisible = visible
        _state.update { s ->
            val all = visible.isNotEmpty() && visible.all { it in s.selected }
            s.copy(isAllSelected = all)
        }
    }

    private fun setExisting(existing: Set<String>) {
        _state.update { s -> s.copy(existing = existing) }
    }

    private fun dispatch(intent: SelectionIntent) {
        _state.update { s -> SelectionReducer.reduce(s, intent, lastVisible) }
    }

    override fun toggleMode(enabled: Boolean) {
        val sc = scope ?: error("SelectionComponent.bindSelection(...) not called")
        if (enabled) {
            sc.launch {
                val persisted = loadPersisted?.invoke() ?: emptySet()
                setExisting(persisted)
                dispatch(SelectionIntent.ToggleMode(true))
            }
        } else {
            dispatch(SelectionIntent.ToggleMode(false))
        }
    }

    override fun setItemChecked(id: String, checked: Boolean) {
        dispatch(SelectionIntent.ToggleOne(id, checked))
    }

    override fun selectAll(checked: Boolean) {
        dispatch(SelectionIntent.ToggleAll(checked))
    }

    override fun cancel() {
        dispatch(SelectionIntent.Cancel)
    }

    override fun confirmStay() {
        val sc = scope ?: error("SelectionComponent.bindSelection(...) not called")
        sc.launch { confirmInternal() }
    }

    override fun confirmProceed() {
        val sc = scope ?: error("SelectionComponent.bindSelection(...) not called")
        sc.launch { confirmInternal() }
    }

    private suspend fun confirmInternal() {
        val s = _state.value
        when (commit?.invoke(s.pendingAdd, s.pendingRemove)) {
            is SelectionCommitResult.Error -> {
                // Keep UI editing state; host VM can surface error via its own effect layer.
            }
            SelectionCommitResult.Noop, SelectionCommitResult.Success -> {
                val newExisting = (s.existing + s.pendingAdd) - s.pendingRemove
                _state.value = SelectionUiState(existing = newExisting)
            }
            null -> { /* no-op if not bound */ }
        }
    }
}
