package com.gpcasiapac.storesystems.feature.collect.presentation.selection

/**
 * Generic result type for committing a selection delta.
 * Presentation layer depends only on this, not on any domain-specific result types.
 */
sealed interface SelectionCommitResult {
    /** No changes needed or nothing to apply. */
    data object Noop : SelectionCommitResult
    /** Commit succeeded. Additional details can be carried by the callee via logs/effects if needed. */
    data object Success : SelectionCommitResult
    /** Commit failed; holds a message that the VM/UI can surface. */
    data class Error(val message: String) : SelectionCommitResult
}
