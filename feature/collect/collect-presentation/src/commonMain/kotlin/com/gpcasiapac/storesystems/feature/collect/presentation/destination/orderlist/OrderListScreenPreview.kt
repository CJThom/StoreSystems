package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestionType
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.sampleCollectOrderStates
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.FilterChip
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
                orderSearchSuggestionList = listOf(
                    OrderSearchSuggestion(text = "Jane", type = OrderSearchSuggestionType.NAME),
                    OrderSearchSuggestion(text = "INV-100", type = OrderSearchSuggestionType.ORDER_NUMBER),
                    OrderSearchSuggestion(text = "+6512345678", type = OrderSearchSuggestionType.PHONE),
                ),
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

            val withFilters = base.copy(
                appliedFilterChipList = listOf(
                    FilterChip(label = "INV-100", type = OrderSearchSuggestionType.ORDER_NUMBER),
                    FilterChip(label = "Jane", type = OrderSearchSuggestionType.NAME),
                ),
                customerTypeFilterList = setOf(CustomerType.B2C),
                filteredCollectOrderStateList = orders.filter { it.customerType == CustomerType.B2C }
            )

            val loading = base.copy(isLoading = true, collectOrderStateList = emptyList(), filteredCollectOrderStateList = emptyList())
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

            return sequenceOf(base, withFilters, multiSelect, multiSelectAll, loading, error)
        }
}

