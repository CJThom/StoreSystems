package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.feedback.haptic.HapticEffect
import com.gpcasiapac.storesystems.common.feedback.sound.SoundEffect
import com.gpcasiapac.storesystems.common.presentation.compose.DialogButton
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.FilterChip
import com.gpcasiapac.storesystems.feature.collect.presentation.selection.SelectionContract
import com.gpcasiapac.storesystems.feature.collect.presentation.selection.SelectionUiState


object OrderListScreenContract {
    @Immutable
    data class State(
        // Data
        val orders: List<CollectOrderListItemState>,

        // Loading / refreshing
        val isLoading: Boolean,
        val isRefreshing: Boolean,

        // Filters & sort
        val filters: Filters,
        val isFilterSheetOpen: Boolean,

        // Shared selection slice (single source of truth for selection UI)
        val selection: SelectionUiState<InvoiceNumber> = SelectionUiState(),

        // Draft bottom bar visibility
        val isDraftBarVisible: Boolean,

        // Derived / info
        val orderCount: Int,                                                    // number of orders ready to collect (from DB or filtered list)

        // Submission / transient UI
        val isSubmitting: Boolean,                                          // e.g., "Submitting order..." snackbar
        val submittedCollectOrder: CollectOrderListItemState?,

        // Error
        val error: String?,
        val dialog: Dialog?
    ) : ViewState {
        @Immutable
        data class Filters(
            val showB2B: Boolean,
            val showB2C: Boolean,
            val sortOption: SortOption,
        )
    }


    sealed interface Event : ViewEvent {
        // User-driven refresh (initial load happens in onStart)
        data object Refresh : Event

        // Navigation / Scanning
        data class OpenOrder(val invoiceNumber: InvoiceNumber) : Event

        data object NavigateToFulfilment : Event
        data class ScanInvoice(val rawInput: String) : Event
        data object Back : Event
        data object Logout : Event
        data object OpenHistory : Event

        // Errors
        data object ClearError : Event

        // Filters & sort
        data class ToggleCustomerType(val type: CustomerType, val checked: Boolean) : Event
        data object OpenFilterSheet : Event
        data object CloseFilterSheet : Event
        data class ApplyFilters(val filterChipList: List<FilterChip>) :
            Event // add chip from search suggestion?

        data class RemoveFilterChip(val filterChipList: FilterChip) : Event
        data object ResetFilters : Event
        data class SortChanged(val sortOption: SortOption) : Event

        // Shared selection wrapper (replaces per-screen selection events)
        data class Selection(val event: SelectionContract.Event<InvoiceNumber>) : Event

        // Search-origin selection confirm
        data class OnAcceptMultiSelectClicked(val fromSearch: Boolean) : Event

        // Draft bottom bar actions
        data object DraftBarDeleteClicked : Event
        data object DraftBarViewClicked : Event

        // Submissions / item actions
        data class SubmitOrder(val invoiceNumber: InvoiceNumber) : Event
        data object StartNewWorkOrderClicked : Event

        // Misc ephemerals
        data object DismissSnackbar : Event
    }

    sealed interface Effect : ViewSideEffect {
        data class ShowSnackbar(
            val message: String,
            val actionLabel: String? = null,
            val duration: SnackbarDuration = SnackbarDuration.Short,
        ) : Effect

        data class PlaySound(val soundEffect: SoundEffect) : Effect
        data class PlayHaptic(val hapticEffect: HapticEffect) : Effect

        // Request the search UI to collapse (triggered by VM on scan)
        data object CollapseSearchBar : Effect

        data object ConfirmSearchSelection : Effect
        data object CancelSearchSelection : Effect

        sealed interface Outcome : Effect {
            data class OrderClicked(val invoiceNumber: InvoiceNumber) : Outcome
            data object RequestNavigateToFulfillment : Outcome
            data object Back : Outcome
            data object Logout : Outcome
            data object OpenHistory : Outcome
        }
    }

    sealed class Dialog {

        data class SearchMultiSelectConfirm(
            val title: String = "Confirm selection",
            val onProceed: DialogButton,
            val onSelect: DialogButton,
            val onCancel: DialogButton,
        ) : Dialog()

//        data class NoGSEMatch(
//            val title: StringWrapper = StringWrapper.Text("Confirm selection"),
//            val onConfirm: DialogButton = DialogButton(),
//            val onDismiss: () -> Unit
//        ) : Dialog()

    }
}
