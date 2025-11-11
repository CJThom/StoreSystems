package com.gpcasiapac.storesystems.feature.collect.presentation.selection

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SelectionHandler<T> : SelectionHandlerDelegate<T> {

    private val _state = MutableStateFlow(SelectionUiState<T>())
    override val state: StateFlow<SelectionUiState<T>> = _state

    private var scope: CoroutineScope? = null
    private var lastVisible: Set<T> = emptySet()
    private var loadPersisted: (suspend () -> Set<T>)? = null
    private var commit: (suspend (Set<T>, Set<T>) -> SelectionCommitResult)? = null
    //private var onRequestConfirmDialog: (() -> Unit)? = null
   // private var onConfirmProceedHook: (() -> Unit)? = null

    override fun bindSelection(
        scope: CoroutineScope,
        visibleIds: Flow<Set<T>>,
        setSelection: (SelectionUiState<T>) -> Unit,
        loadPersisted: suspend () -> Set<T>,
        commit: suspend (toAdd: Set<T>, toRemove: Set<T>) -> SelectionCommitResult,
    ) {
        this.scope = scope
        this.loadPersisted = loadPersisted
        this.commit = commit
      //  this.onRequestConfirmDialog = onRequestConfirmDialog
     //   this.onConfirmProceedHook = onConfirmProceed

        scope.launch { visibleIds.collectLatest { onVisibleIdsChanged(it) } }
        scope.launch { state.collectLatest { sel -> setSelection(sel) } }
    }

    private fun onVisibleIdsChanged(visible: Set<T>) {
        lastVisible = visible
        _state.update { s ->
            val all = visible.isNotEmpty() && visible.all { it in s.selected }
            s.copy(isAllSelected = all)
        }
    }

    private fun setExisting(existing: Set<T>) {
        _state.update { s -> s.copy(existing = existing) }
    }

    private fun dispatch(intent: SelectionIntent<T>) {
        _state.update { s -> SelectionReducer.reduce(s, intent, lastVisible) }
    }

    override fun handleSelection(event: SelectionContract.Event<T>) {
        when (event) {
            is SelectionContract.Event.ToggleMode -> toggleMode(event.enabled)
            is SelectionContract.Event.SetItemChecked -> setItemChecked(event.id, event.checked)
            is SelectionContract.Event.SelectAll -> selectAll(event.checked)
            SelectionContract.Event.Cancel -> cancel()
            SelectionContract.Event.Confirm -> confirm()
        }
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

    override fun setItemChecked(id: T, checked: Boolean) {
        dispatch(SelectionIntent.ToggleOne(id, checked))
    }

    override fun selectAll(checked: Boolean) {
        dispatch(SelectionIntent.ToggleAll(checked))
    }

    override fun cancel() {
        dispatch(SelectionIntent.Cancel)
    }

//    override fun confirmStay() {
//        val sc = scope ?: error("SelectionComponent.bindSelection(...) not called")
//        sc.launch { confirmInternal() }
//    }

    override fun confirm() {
        val sc = scope ?: error("SelectionComponent.bindSelection(...) not called")
        sc.launch {
            confirmInternal()
            // onConfirmProceedHook?.invoke() }
        }
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
