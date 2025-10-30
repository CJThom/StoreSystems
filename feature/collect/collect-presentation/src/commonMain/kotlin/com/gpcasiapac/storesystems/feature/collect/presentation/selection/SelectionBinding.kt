package com.gpcasiapac.storesystems.feature.collect.presentation.selection

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * Backwards-compatible binding helper. Prefer calling SelectionDelegate.bindSelection(...) directly.
 */
@Deprecated("Use SelectionDelegate.bindSelection(...) directly")
object SelectionBinding {

    /** A group of ready-to-use handler functions for the ViewModel to call from events. */
    class Handlers internal constructor(
        private val delegate: SelectionHandlerDelegate,
    ) {
        fun toggleMode(enabled: Boolean) = delegate.toggleMode(enabled)
        fun checkOne(id: String, checked: Boolean) = delegate.setItemChecked(id, checked)
        fun selectAll(checked: Boolean) = delegate.selectAll(checked)
        fun cancel() = delegate.cancel()
        fun confirmStay() = delegate.confirmStay()
        fun confirmProceed() = delegate.confirmProceed()
    }

    /**
     * Bind the delegate to a visibleIds flow and start mirroring state into the host via setSelection.
     */
    fun bind(
        scope: CoroutineScope,
        delegate: SelectionHandlerDelegate,
        visibleIds: Flow<Set<String>>,
        setSelection: (SelectionUiState) -> Unit,
        loadPersisted: suspend () -> Set<String>,
        commit: suspend (toAdd: Set<String>, toRemove: Set<String>) -> SelectionCommitResult,
    ): Handlers {
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
