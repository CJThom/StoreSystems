package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import com.gpcasiapac.storesystems.common.feedback.haptic.HapticEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.Representative
import com.gpcasiapac.storesystems.feature.collect.presentation.component.CollectionTypeSectionDisplayState
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CorrespondenceItemDisplayParam
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.util.DebounceKey
import com.gpcasiapac.storesystems.feature.collect.presentation.util.DebouncePreset
import com.gpcasiapac.storesystems.feature.collect.presentation.util.DebouncerDefaults

object OrderFulfilmentScreenContract {


    @Immutable
    data class State(
        val collectOrderListItemStateList: List<CollectOrderListItemState>,

        // Loading / refreshing
        val isLoading: Boolean,
        
        // Processing confirmation (adding to sync queue)
        val isProcessing: Boolean,

        val error: String?,
        val featureFlags: FeatureFlags,

        // Who's collecting
        val collectingType: CollectingType,
        val collectionTypeOptionList: List<CollectionTypeSectionDisplayState>,

        // Account flow
        val representativeSearchQuery: String,
        val representativeList: List<Representative>,
        val selectedRepresentativeIds: Set<String>,

        // Courier flow
        val courierName: String,

        // Signature data (stores actual signature strokes for preview)
        val signatureStrokes: List<List<Offset>>,
        // Signature image as Base64 (observed from Work Order)
        val signatureBase64: String? = null,

        // Customer name capture dialog
        val isCustomerNameDialogVisible: Boolean = false,
        val customerNameInput: String = "",

        // Correspondence
        val correspondenceOptionList: List<CorrespondenceItemDisplayParam>,
    ) : ViewState{

        data class FeatureFlags(
            val isAccountCollectingFeatureEnabled: Boolean,
            val isCorrespondenceSectionVisible: Boolean
        )

    }

    sealed interface Event : ViewEvent {
        // Data loading
        data object Refresh : Event

        // Errors & navigation
        data object ClearError : Event/**/
        data object Back : Event
        // Back confirmation dialog actions
        data object ConfirmBackSave : Event
        data object ConfirmBackDiscard : Event
        data object CancelBackDialog : Event

        // Collecting selector
        data class CollectingChanged(val type: CollectingType) : Event

        // Account flow
        data class RepresentativeSearchQueryChanged(val query: String) : Event
        data class RepresentativeSelected(val id: String, val isSelected: Boolean) : Event
        data object ClearRepresentativeSelection : Event

        // Courier flow
        data class CourierNameChanged(val text: String) : Event
        data object ClearCourierName : Event

        // Signature
        data object Sign : Event
        // Customer name dialog flow
        data object ShowCustomerNameDialog : Event
        data object DismissCustomerNameDialog : Event
        data class CustomerNameChanged(val text: String) : Event
        data object ConfirmCustomerName : Event
        data class SignatureSaved(val strokes: List<List<Offset>>) : Event
        data object ClearSignature : Event

        // Correspondence
        data class ToggleCorrespondence(val id: String) : Event
        data class EditCorrespondence(val id: String) : Event

        // Final action
        data object Confirm : Event

        // Search-origin selection confirmation
        data object ConfirmSearchSelection : Event
        data object ConfirmSearchSelectionProceed : Event
        data object DismissConfirmSearchSelectionDialog : Event

        // Order item click
        data class OrderClicked(val invoiceNumber: String) : Event

        // Scanning
        data class ScanInvoice(val invoiceNumber: String, val autoSelect: Boolean) : Event

        // Deselect an order from Fulfilment item actions
        data class DeselectOrder(val invoiceNumber: String) : Event
    }

    sealed interface Effect : ViewSideEffect {
        data class ShowSnackbar(
            val message: String,
            val actionLabel: String? = null,
            val duration: androidx.compose.material3.SnackbarDuration = androidx.compose.material3.SnackbarDuration.Short,
        ) : Effect
        data class PlaySound(val soundEffect: com.gpcasiapac.storesystems.common.feedback.sound.SoundEffect) : Effect
        data class PlayHaptic(val type: HapticEffect) : Effect
        // Ask UI to present a 3-button dialog for unsaved progress
        data class ShowSaveDiscardDialog(
            val title: String = "Unsaved progress",
            val message: String = "You have unsaved changes. Save as draft or discard?",
            val saveLabel: String = "Save",
            val discardLabel: String = "Discard",
            val cancelLabel: String = "Cancel",
        ) : Effect

        // Two-button confirm dialog for selection coming from Search in Fulfilment
        data class ShowConfirmSelectionDialog(
            val title: String = "Confirm selection",
            val confirmLabel: String = "Confirm",
            val cancelLabel: String = "Cancel",
        ) : Effect

        // Request the search UI to collapse (triggered by VM on scan)
        data object CollapseSearchBar : Effect
 
         sealed interface Outcome : Effect {
            data object Back : Outcome
            data object Confirmed : Outcome
            data class SignatureRequested(val customerName: String): Outcome
            data class NavigateToOrderDetails(val invoiceNumber: String) : Outcome
            data object SaveAndExit : Outcome
            data object DiscardAndExit : Outcome
        }
    }

    /** Preset combinations to use at call sites for this screen. */
    sealed class Debounce(
        final override val key: DebounceKey,
        final override val interval: DebouncerDefaults.Interval
    ) : DebouncePreset {
        data object CollectingType : Debounce(
            DebounceKey.CollectingType,
            DebouncerDefaults.Interval.Medium
        )
        data object CourierName : Debounce(
            DebounceKey.CourierName,
            DebouncerDefaults.Interval.Medium
        )
    }
}