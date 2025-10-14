package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Representative
import com.gpcasiapac.storesystems.feature.collect.presentation.component.CollectionTypeSectionDisplayState
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CorrespondenceItemDisplayParam
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model.CollectOrderWithCustomerWithLineItemsState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState

object OrderFulfilmentScreenContract {

    @Immutable
    data class State(
        val collectOrderWithCustomerWithLineItemsState: CollectOrderWithCustomerWithLineItemsState?,
        val collectOrderListItemStateList: List<CollectOrderListItemState>,

        // Loading / refreshing
        val isLoading: Boolean,

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

        // Correspondence
        val correspondenceOptionList: List<CorrespondenceItemDisplayParam>,

        // Product list
        val visibleProductListItemCount: Int,
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
        data class SignatureSaved(val strokes: List<List<Offset>>) : Event
        data object ClearSignature : Event

        // Correspondence
        data class ToggleCorrespondence(val id: String) : Event
        data class EditCorrespondence(val id: String) : Event

        // Final action
        data object Confirm : Event

        // Product list
        data object ToggleProductListExpansion : Event

        // Order item click
        data class OrderClicked(val invoiceNumber: String) : Event
    }

    sealed interface Effect : ViewSideEffect {
        data class ShowToast(val message: String) : Effect
        data class ShowError(val error: String) : Effect
        // Ask UI to present a 3-button dialog for unsaved progress
        data class ShowSaveDiscardDialog(
            val title: String = "Unsaved progress",
            val message: String = "You have unsaved changes. Save as draft or discard?",
            val saveLabel: String = "Save",
            val discardLabel: String = "Discard",
            val cancelLabel: String = "Cancel",
        ) : Effect

        sealed interface Outcome : Effect {
            data object Back : Outcome
            data object Confirmed : Outcome
            data object SignatureRequested: Outcome
            data class NavigateToOrderDetails(val invoiceNumber: String) : Outcome
            data object SaveAndExit : Outcome
            data object DiscardAndExit : Outcome
        }
    }
}