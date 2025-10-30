package com.gpcasiapac.storesystems.feature.collect.presentation.selection

import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect

/**
 * Shared selection feature contract: reusable events/effects across screens.
 */
object SelectionContract {
    sealed interface Event : ViewEvent {
        data class ToggleMode(val enabled: Boolean) : Event
        data class SetItemChecked(val id: String, val checked: Boolean) : Event
        data class SelectAll(val checked: Boolean) : Event
        data object Cancel : Event
        // Optional dialog triggers/actions
        data object Confirm : Event
        data object ConfirmStay : Event
        data object ConfirmProceed : Event
        data object DismissConfirmDialog : Event
    }

    // If needed later, shared effects can be added here
    sealed interface Effect : ViewSideEffect
}
