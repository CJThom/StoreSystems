package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.HapticType
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestionType
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.FilterChip


object OrderListScreenContract {
    @Immutable
    data class State(
        // Data
        val orders: List<CollectOrderListItemState>,

        // Loading / refreshing
        val isLoading: Boolean,
        val isRefreshing: Boolean,

        // Filters & sort
        val customerTypeFilterList: Set<CustomerType>,
        val isFilterSheetOpen: Boolean,
        val sortOption: SortOption,

        // Selection mode
        val isMultiSelectionEnabled: Boolean,                               // selection mode enabled (checkboxes visible)
        val selectedOrderIdList: Set<String>,                                // derived UI-checked = (existing - pendingRemove) ∪ pendingAdd
        val isSelectAllChecked: Boolean,

        // Normalized selection-editing model (lives entirely in ViewState)
        val existingDraftIdSet: Set<String>,                                 // snapshot of persisted IDs at entry into multi-select
        val pendingAddIdSet: Set<String>,                                    // newly selected in this session (not yet persisted)
        val pendingRemoveIdSet: Set<String>,                                 // previously persisted but unchecked in this session (not yet removed)

        // Draft bottom bar visibility
        val isDraftBarVisible: Boolean,

        // Derived / info
        val orderCount: Int,                                                    // number of orders ready to collect (from DB or filtered list)

        // Submission / transient UI
        val isSubmitting: Boolean,                                          // e.g., "Submitting order..." snackbar
        val submittedCollectOrder: CollectOrderListItemState?,

        // Error
        val error: String?,
    ) : ViewState


    sealed interface Event : ViewEvent {
        // User-driven refresh (initial load happens in onStart)
        data object Refresh : Event

        // Navigation / Scanning
        data class OpenOrder(val orderId: String) : Event
        data class ScanInvoice(val invoiceNumber: String) : Event
        data object Back : Event
        data object Logout : Event

        // Errors
        data object ClearError : Event

        // Filters & sort
        data class ToggleCustomerType(val type: CustomerType, val checked: Boolean) : Event
        data object OpenFilterSheet : Event
        data object CloseFilterSheet : Event
        data class ApplyFilters(val filterChipList: List<FilterChip>) : Event // add chip from search suggestion?
        data class RemoveFilterChip(val filterChipList: FilterChip) : Event
        data object ResetFilters : Event
        data class SortChanged(val sortOption: SortOption) : Event

        // Selection mode & actions
        data class ToggleSelectionMode(val enabled: Boolean) : Event
        data class OrderChecked(val orderId: String, val checked: Boolean) : Event
        data class SelectAll(val checked: Boolean) : Event
        data object CancelSelection : Event
        data object ConfirmSelection : Event
        // Search-origin selection confirm
        data object ConfirmSearchSelection : Event
        // Dialog actions for multi-select confirmation
        data object ConfirmSelectionStay : Event
        data object ConfirmSelectionProceed : Event
        data object DismissConfirmSelectionDialog : Event

        // Draft bottom bar actions
        data object DraftBarDeleteClicked : Event
        data object DraftBarViewClicked : Event

        // Submissions / item actions
        data class SubmitOrder(val orderId: String) : Event
        data object SubmitSelectedOrders : Event
        data object StartNewWorkOrderClicked : Event

        // Misc ephemerals
        data object DismissSnackbar : Event
    }

    sealed interface Effect : ViewSideEffect {
        data class ShowToast(val message: String) : Effect
        data class ShowError(val error: String) : Effect
        data class ShowSnackbar(
            val message: String,
            val actionLabel: String? = null,
            val persistent: Boolean = false,
        ) : Effect
        data object PlayErrorSound : Effect
        data class Haptic(val type: HapticType) : Effect
        data class OpenDialer(val phoneNumber: String) : Effect
        data class CopyToClipboard(val label: String, val text: String) : Effect

        // Multi-select confirmation dialog trigger
        data class ShowMultiSelectConfirmDialog(
            val title: String = "Confirm selection",
            val cancelLabel: String = "Cancel",
            val selectOnlyLabel: String = "Select only",
            val proceedLabel: String = "Select and proceed",
        ) : Effect

        // Search-origin multi-select confirmation dialog trigger
        data class ShowSearchMultiSelectConfirmDialog(
            val title: String = "Confirm selection",
            val cancelLabel: String = "Cancel",
            val selectOnlyLabel: String = "Select only",
            val proceedLabel: String = "Select and proceed",
        ) : Effect

        sealed interface Outcome : Effect {
            data class OrderSelected(val invoiceNumber: String) : Outcome
            data object OrdersSelected : Outcome
            data object Back : Outcome
            data object Logout : Outcome
        }
    }
}
