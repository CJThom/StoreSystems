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
        val orderList: List<Order> = emptyList(),                                   // full dataset observed
        val filteredOrderList: List<Order> = emptyList(),                           // single list rendered (optional if DB-side)

        // Loading / refreshing
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,

        // Search
        val searchText: String = "",
        val isSearchActive: Boolean = false,                                        // search bar expanded with suggestions
        val orderSearchSuggestions: List<OrderSearchSuggestion> = emptyList(),                // the overlay list items

        // Filter chips & toggles
        val customerTypeFilters: Set<CustomerType> = setOf(CustomerType.B2B, CustomerType.B2C),
        val appliedFilterChips: List<FilterChip> = emptyList(),                     // e.g., phone chip from screenshots
        val isFilterSheetOpen: Boolean = false,
        val sortOption: SortOption = SortOption.TIME_WAITING_DESC,

        // Selection mode
        val isMultiSelectionEnabled: Boolean = false,                               // selection mode enabled (checkboxes visible)
        val selectedOrderIdList: Set<String> = emptySet(),
        val isSelectAllChecked: Boolean = false,

        // Derived / info
        val orderCount: Int = 0,                                                    // number of orders ready to collect (from DB or filtered list)

        // Submission / transient UI
        val isSubmitting: Boolean = false,                                          // e.g., "Submitting order..." snackbar
        val submittedOrder: Order? = null,

        // Suggestions from old shape (kept for compatibility if used elsewhere)
        val searchHintResultList: List<Order> = emptyList(),

        // Error
        val error: String? = null,
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
        data class ApplyFilters(val chips: List<FilterChip>) : Event
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
