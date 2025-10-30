package com.gpcasiapac.storesystems.feature.collect.presentation.selection

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Single, easy-to-use surface for selection. Keeps logic split (pure reducer + component),
 * but presents one API to ViewModels.
 */
interface SelectionHandlerDelegate {
    val state: StateFlow<SelectionUiState>

    /** One-call binding to wire visible ids, mirror state, and provide persistence hooks. */
    fun bindSelection(
        scope: CoroutineScope,
        visibleIds: Flow<Set<String>>,
        setSelection: (SelectionUiState) -> Unit,
        loadPersisted: suspend () -> Set<String>,
        commit: suspend (toAdd: Set<String>, toRemove: Set<String>) -> SelectionCommitResult,
        onRequestConfirmDialog: (() -> Unit)? = null,
        onConfirmProceed: (() -> Unit)? = null,
    )

    /** Unified entry point to handle shared selection events. */
    fun handleSelection(event: SelectionContract.Event)

    // Handlers the VM can call directly (no extra handler field needed)
    fun toggleMode(enabled: Boolean)
    fun setItemChecked(id: String, checked: Boolean)
    fun selectAll(checked: Boolean)
    fun cancel()
    fun confirmStay()
    fun confirmProceed()
}
