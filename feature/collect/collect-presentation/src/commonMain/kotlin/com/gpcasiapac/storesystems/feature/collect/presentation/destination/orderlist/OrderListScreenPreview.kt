package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import com.gpcasiapac.storesystems.feature.collect.domain.model.Customer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestionType
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.FilterChip
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private val now get() = Clock.System.now()

 fun sampleOrders(): List<Order> = listOf(
    Order(
        id = "1",
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        pickedAt = now - 2.days,
        customer = Customer(
            customerNumber = "CUST-0001",
            customerType = CustomerType.B2C,
            accountName = null,
            firstName = "Johnathan",
            lastName = "Citizenship",
            phone = "+65 8123 4567",
        )
    ),
    Order(
        id = "2",
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        pickedAt = now - 2.hours,
        customer = Customer(
            customerNumber = "ACC-0002",
            customerType = CustomerType.B2B,
            accountName = "ABC Motorsports PTY Limited",
            firstName = null,
            lastName = null,
            phone = "+65 8000 0002",
        )
    ),
    Order(
        id = "3",
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        pickedAt = now - 45.minutes,
        customer = Customer(
            customerNumber = "ACC-0003",
            customerType = CustomerType.B2B,
            accountName = "AU Mechanics",
            firstName = null,
            lastName = null,
            phone = "+65 8000 0003",
        )
    ),
    Order(
        id = "4",
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        pickedAt = now - 30.minutes,
        customer = Customer(
            customerNumber = "CUST-0004",
            customerType = CustomerType.B2C,
            accountName = null,
            firstName = "Jane",
            lastName = "Doe",
            phone = "+65 8123 4568",
        )
    ),
    Order(
        id = "5",
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        pickedAt = now - 15.minutes,
        customer = Customer(
            customerNumber = "CUST-0005",
            customerType = CustomerType.B2C,
            accountName = null,
            firstName = "John",
            lastName = "Doe",
            phone = "+65 8123 4569",
        )
    ),
    Order(
        id = "6",
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        pickedAt = now - 5.minutes,
        customer = Customer(
            customerNumber = "CUST-0006",
            customerType = CustomerType.B2C,
            accountName = null,
            firstName = "Bob",
            lastName = "Jane",
            phone = "+65 8123 4570",
        )
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
                filteredOrderList = orders.filter { it.customer.customerType == CustomerType.B2C }
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

