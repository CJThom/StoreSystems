package com.gpcasiapac.storesystems.feature.collect.presentation.selection

import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect

/**
 * Shared selection feature contract: reusable events/effects across screens.
 */
object SelectionContract {
    sealed interface Event<out T> : ViewEvent {
        data class ToggleMode(val enabled: Boolean) : Event<Nothing>
        data class SetItemChecked<T>(val id: T, val checked: Boolean) : Event<T>
        data class SelectAll(val checked: Boolean) : Event<Nothing>
        data object Cancel : Event<Nothing>
        // Optional dialog triggers/actions
        data object Confirm : Event<Nothing>
        data object ConfirmStay : Event<Nothing>
        data object ConfirmProceed : Event<Nothing>
        data object DismissConfirmDialog : Event<Nothing>
    }

    // If needed later, shared effects can be added here
    sealed interface Effect : ViewSideEffect
}
