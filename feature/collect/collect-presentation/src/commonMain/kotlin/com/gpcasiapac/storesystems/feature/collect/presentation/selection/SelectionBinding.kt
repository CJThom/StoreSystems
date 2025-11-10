package com.gpcasiapac.storesystems.feature.collect.presentation.selection

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * Backwards-compatible binding helper. Prefer calling SelectionDelegate.bindSelection(...) directly.
 */
@Deprecated("Use SelectionDelegate.bindSelection(...) directly")
object SelectionBinding {

    /** A group of ready-to-use handler functions for the ViewModel to call from events. */
    class Handlers<T> internal constructor(
        private val delegate: SelectionHandlerDelegate<T>,
    ) {
        fun toggleMode(enabled: Boolean) = delegate.toggleMode(enabled)
        fun checkOne(id: T, checked: Boolean) = delegate.setItemChecked(id, checked)
        fun selectAll(checked: Boolean) = delegate.selectAll(checked)
        fun cancel() = delegate.cancel()
       // fun confirmProceed() = delegate.confirmProceed()
    }

    /**
     * Bind the delegate to a visibleIds flow and start mirroring state into the host via setSelection.
     */
    fun <T> bind(
        scope: CoroutineScope,
        delegate: SelectionHandlerDelegate<T>,
        visibleIds: Flow<Set<T>>,
        setSelection: (SelectionUiState<T>) -> Unit,
        loadPersisted: suspend () -> Set<T>,
        commit: suspend (toAdd: Set<T>, toRemove: Set<T>) -> SelectionCommitResult,
    ): Handlers<T> {
        delegate.bindSelection(
            scope = scope,
            visibleIds = visibleIds,
            setSelection = setSelection,
            loadPersisted = loadPersisted,
            commit = commit,
        )
        return Handlers(delegate)
    }
}
