package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestionType
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.FilterChip
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private val now get() = Clock.System.now()

 fun sampleOrders(): List<Order> = listOf(
    Order(
        id = "1",
        customerType = CustomerType.B2C,
        customerName = "Jane Doe",
        invoiceNumber = "INV-1001",
        webOrderNumber = "WEB-7771",
        pickedAt = now - 12.minutes
    ),
    Order(
        id = "2",
        customerType = CustomerType.B2B,
        customerName = "Acme Corp",
        invoiceNumber = "INV-1002",
        webOrderNumber = null,
        pickedAt = now - 2.hours
    ),
    Order(
        id = "3",
        customerType = CustomerType.B2C,
        customerName = "John Smith",
        invoiceNumber = "INV-1003",
        webOrderNumber = "WEB-7773",
        pickedAt = now - 1.hours
    ),
)

class OrderListScreenStateProvider : PreviewParameterProvider<OrderListScreenContract.State> {
    override val values: Sequence<OrderListScreenContract.State>
        get() {
            val orders = sampleOrders()

            val base = OrderListScreenContract.State(
                orderList = orders,
                filteredOrderList = orders,
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
                submittedOrder = null,
                searchHintResultList = emptyList(),
                error = null,
            )

            val withFilters = base.copy(
                appliedFilterChipList = listOf(
                    FilterChip(label = "INV-100", type = OrderSearchSuggestionType.ORDER_NUMBER),
                    FilterChip(label = "Jane", type = OrderSearchSuggestionType.NAME),
                ),
                customerTypeFilterList = setOf(CustomerType.B2C),
                filteredOrderList = orders.filter { it.customerType == CustomerType.B2C }
            )

            val loading = base.copy(isLoading = true, orderList = emptyList(), filteredOrderList = emptyList())
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

