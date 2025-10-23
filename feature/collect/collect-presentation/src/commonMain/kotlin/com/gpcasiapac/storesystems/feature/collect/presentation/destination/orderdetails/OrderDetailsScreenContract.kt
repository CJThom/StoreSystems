package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetails

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.feedback.haptic.HapticEffect
import com.gpcasiapac.storesystems.common.feedback.sound.SoundEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model.CollectOrderWithCustomerWithLineItemsState

object OrderDetailsScreenContract {

    @Immutable
    data class State(
        val order: CollectOrderWithCustomerWithLineItemsState?,
        val isLoading: Boolean,
        val error: String?,
    ) : ViewState

    sealed interface Event : ViewEvent {
        data class Refresh(val invoiceNumber: String) : Event
        data object Back : Event
        data object Select : Event
        // New: handle scans directly on the details screen
        data class ScanInvoice(val invoiceNumber: String) : Event
    }

    sealed interface Effect : ViewSideEffect {
        // Platform feedback + snackbar for invalid scans
        data class ShowSnackbar(
            val message: String,
            val actionLabel: String? = null,
            val duration: SnackbarDuration = SnackbarDuration.Short,
        ) : Effect
        data class PlaySound(val soundEffect: SoundEffect) : Effect
        data class PlayHaptic(val hapticEffect: HapticEffect) : Effect

        sealed interface Outcome : Effect {
            data object Back : Outcome
            data class Selected(val invoiceNumber: String) : Outcome
            // New: Navigate to another order details (re-open with new invoice)
            data class OrderSelected(val invoiceNumber: String) : Outcome
        }
    }
}