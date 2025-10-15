package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestionType
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.FilterChip
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.sampleCollectOrderListItemStateList
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

class OrderListScreenStateProvider : PreviewParameterProvider<OrderListScreenContract.State> {
    override val values: Sequence<OrderListScreenContract.State>
        get() {
            val orders = sampleCollectOrderListItemStateList()

            val base = OrderListScreenContract.State(
                orders = orders,
                searchResults = emptyList(),
                isLoading = false,
                isRefreshing = false,
                searchText = "",
                isSearchActive = false,
                orderSearchSuggestionList = emptyList(),
                customerTypeFilterList = setOf(CustomerType.B2B, CustomerType.B2C),
                isFilterSheetOpen = false,
                sortOption = SortOption.TIME_WAITING_DESC,
                isMultiSelectionEnabled = false,
                selectedOrderIdList = emptySet(),
                isSelectAllChecked = false,
                existingDraftIdSet = emptySet(),
                pendingAddIdSet = emptySet(),
                pendingRemoveIdSet = emptySet(),
                isDraftBarVisible = false,
                orderCount = orders.size,
                isSubmitting = false,
                submittedCollectOrder = null,
                error = null,
            )

            val collapsedSearchBar = base.copy(
                searchText = "",
                isSearchActive = false,
                orderSearchSuggestionList = emptyList()
            )

            val expandedSearchBar = base.copy(
                searchText = "John",
                isSearchActive = true,
                orderSearchSuggestionList = listOf(
                    OrderSearchSuggestion(text = "Order #12345 - John Doe", type = OrderSearchSuggestionType.NAME),
                    OrderSearchSuggestion(text = "Order #12346 - John Smith", type = OrderSearchSuggestionType.NAME),
                    OrderSearchSuggestion(text = "Order #12347 - John Williams", type = OrderSearchSuggestionType.NAME),
                    OrderSearchSuggestion(text = "Order #12348 - Johnny Cash", type = OrderSearchSuggestionType.NAME),
                )
            )

            val withFilters = base.copy(
                customerTypeFilterList = setOf(CustomerType.B2C),
                orders = orders.filter { it.customerType == CustomerType.B2C }
            )

            val loading = base.copy(
                isLoading = true,
                orders = orders
            )
            val refreshing = base.copy(
                isRefreshing = true,
                orders = orders
            )
            val error = base.copy(error = "Failed to load orders")

            val multiSelect = base.copy(
                isMultiSelectionEnabled = true,
                selectedOrderIdList = setOf(orders[0].invoiceNumber, orders[2].invoiceNumber),
                isSelectAllChecked = false
            )

            val multiSelectAll = base.copy(
                isMultiSelectionEnabled = true,
                selectedOrderIdList = orders.map { it.invoiceNumber }.toSet(),
                isSelectAllChecked = true
            )

            return sequenceOf(
                collapsedSearchBar,
                expandedSearchBar,
                withFilters,
                multiSelect,
                multiSelectAll,
                loading,
                refreshing,
                error
            )
        }
}

