package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Representative
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderState

object OrderDetailScreenContract {

    @Immutable
    data class State(
        // Single order context (B2C single). If multiple, use [orderList]
        val orderId: String?,
        val collectOrder: CollectOrderState?,

        // Multiple order context (B2C multi, B2B/account multi, Courier multi)
        val collectOrderList: List<CollectOrderState>,

        // Top-level flags
        val isLoading: Boolean,
        val error: String?,

        // Who's collecting
        val collectingType: CollectingType,

        // Account flow
        val representativeSearchText: String,
        val recentRepresentativeList: List<Representative>,
        val selectedRepresentativeIdList: Set<String>,

        // Courier flow
        val courierName: String,

        // Signature data (stores actual signature strokes for preview)
        val signatureStrokes: List<List<Offset>>,

        // Correspondence
        val emailChecked: Boolean,
        val printChecked: Boolean,
    ) : ViewState

    sealed interface Event : ViewEvent {
        // Data loading
        data object Refresh : Event

        // Errors & navigation
        data object ClearError : Event
        data object Back : Event

        // Collecting selector
        data class CollectingChanged(val type: CollectingType) : Event

        // Account flow
        data class RepresentativeSearchChanged(val text: String) : Event
        data class RepresentativeChecked(val representativeId: String, val checked: Boolean) : Event
        data object ClearRepresentativeSelection : Event

        // Courier flow
        data class CourierNameChanged(val text: String) : Event
        data object ClearCourierName : Event

        // Signature
        data object Sign : Event
        data class SignatureSaved(val strokes: List<List<Offset>>) : Event
        data object ClearSignature : Event

        // Correspondence
        data class ToggleEmail(val checked: Boolean) : Event
        data class TogglePrint(val checked: Boolean) : Event
        data object EditEmail : Event
        data object EditPrinter : Event

        // Final action
        data object Confirm : Event
    }

    sealed interface Effect : ViewSideEffect {
        data class ShowToast(val message: String) : Effect
        data class ShowError(val error: String) : Effect

        sealed interface Outcome : Effect {
            data object Back : Outcome
            data object Confirmed : Outcome

            data object SignatureRequested: Outcome
        }
    }
}
