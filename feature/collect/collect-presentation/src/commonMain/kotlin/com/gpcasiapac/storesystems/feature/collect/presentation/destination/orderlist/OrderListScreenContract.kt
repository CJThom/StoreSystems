package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.HapticType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestionType
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.FilterChip


object OrderListScreenContract {
    @Immutable
    data class State(
        // Data
        val orderList: List<Order>,                                   // full dataset observed
        val filteredOrderList: List<Order>,                           // single list rendered (optional if DB-side)
        
        // Loading / refreshing
        val isLoading: Boolean,
        val isRefreshing: Boolean,
        
        // Search
        val searchText: String,
        val isSearchActive: Boolean,                                        // search bar expanded with suggestions
        val orderSearchSuggestionList: List<OrderSearchSuggestion>,                // the overlay list items
        
        // Filter chips & toggles
        val customerTypeFilterList: Set<CustomerType>,
        val appliedFilterChipList: List<FilterChip>,                     // e.g., phone chip from screenshots
        val isFilterSheetOpen: Boolean,
        val sortOption: SortOption,
        
        // Selection mode
        val isMultiSelectionEnabled: Boolean,                               // selection mode enabled (checkboxes visible)
        val selectedOrderIdList: Set<String>,
        val isSelectAllChecked: Boolean,
        
        // Derived / info
        val orderCount: Int,                                                    // number of orders ready to collect (from DB or filtered list)
        
        // Submission / transient UI
        val isSubmitting: Boolean,                                          // e.g., "Submitting order..." snackbar
        val submittedOrder: Order?,
        
        // Suggestions from old shape (kept for compatibility if used elsewhere)
        val searchHintResultList: List<Order>,
        
        // Error
        val error: String?,
    ) : ViewState

    sealed interface Event : ViewEvent {
        // User-driven refresh (initial load happens in onStart)
        data object Refresh : Event

        // Navigation
        data class OpenOrder(val orderId: String) : Event
        data object Back : Event

        // Errors
        data object ClearError : Event

        // Search & suggestions
        data class SearchTextChanged(val text: String) : Event
        data class SearchActiveChanged(val active: Boolean) : Event
        data object ClearSearch : Event
        data class SearchSuggestionClicked(val suggestion: String, val type: OrderSearchSuggestionType) : Event

        // Filters & sort
        data class ToggleCustomerType(val type: CustomerType, val checked: Boolean) : Event
        data object OpenFilterSheet : Event
        data object CloseFilterSheet : Event
        data class ApplyFilters(val chips: List<FilterChip>) : Event // add chip from search suggestion?
        data class RemoveFilterChip(val chip: FilterChip) : Event
        data object ResetFilters : Event
        data class SortChanged(val option: SortOption) : Event

        // Selection mode & actions
        data class ToggleSelectionMode(val enabled: Boolean) : Event
        data class OrderChecked(val orderId: String, val checked: Boolean) : Event
        data class SelectAll(val checked: Boolean) : Event
        data object CancelSelection : Event
        data object ConfirmSelection : Event

        // Submissions / item actions
        data class SubmitOrder(val orderId: String) : Event
        data object SubmitSelectedOrders : Event

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
        data class Haptic(val type: HapticType) : Effect
        data class OpenDialer(val phoneNumber: String) : Effect
        data class CopyToClipboard(val label: String, val text: String) : Effect

        sealed interface Outcome : Effect {
            data class OrderSelected(val orderId: String) : Outcome
            data class OrdersSelected(val orderIds: List<String>) : Outcome
            data object Back : Outcome
        }
    }
}
