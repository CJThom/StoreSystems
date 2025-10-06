package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.compose.runtime.Composable
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestionType
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.sampleCollectOrderStates
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.FilterChip
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

class OrderListScreenStateProvider : PreviewParameterProvider<OrderListScreenContract.State> {
    override val values: Sequence<OrderListScreenContract.State>
        get() {
            val orders = sampleCollectOrderStates()

            val base = OrderListScreenContract.State(
                collectOrderStateList = orders,
                filteredCollectOrderStateList = orders,
                isLoading = false,
                isRefreshing = false,
                searchText = "",
                isSearchActive = false,
                orderSearchSuggestionList = emptyList(),
                customerTypeFilterList = setOf(CustomerType.B2B, CustomerType.B2C),
                appliedFilterChipList = emptyList(),
                isFilterSheetOpen = false,
                sortOption = SortOption.TIME_WAITING_DESC,
                isMultiSelectionEnabled = false,
                selectedOrderIdList = emptySet(),
                isSelectAllChecked = false,
                orderCount = orders.size,
                isSubmitting = false,
                submittedCollectOrder = null,
                searchHintResultList = emptyList(),
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
                appliedFilterChipList = listOf(
                    FilterChip(label = "INV-100", type = OrderSearchSuggestionType.ORDER_NUMBER),
                    FilterChip(label = "Jane", type = OrderSearchSuggestionType.NAME),
                ),
                customerTypeFilterList = setOf(CustomerType.B2C),
                filteredCollectOrderStateList = orders.filter { it.customerType == CustomerType.B2C }
            )

            val loading = base.copy(
                isLoading = true,
                collectOrderStateList = orders,
                filteredCollectOrderStateList = emptyList()
            )
            val refreshing = base.copy(
                isRefreshing = true,
                collectOrderStateList = orders,
                filteredCollectOrderStateList = emptyList()
            )
            val error = base.copy(error = "Failed to load orders")

            val multiSelect = base.copy(
                isMultiSelectionEnabled = true,
                selectedOrderIdList = setOf(orders[0].id, orders[2].id),
                isSelectAllChecked = false
            )

            val multiSelectAll = base.copy(
                isMultiSelectionEnabled = true,
                selectedOrderIdList = orders.map { it.id }.toSet(),
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

