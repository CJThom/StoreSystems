package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import com.gpcasiapac.storesystems.feature.collect.presentation.selection.SelectionUiState

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.sampleCollectOrderListItemStateList

class OrderListScreenStateProvider : PreviewParameterProvider<OrderListScreenContract.State> {
    override val values: Sequence<OrderListScreenContract.State>
        get() {
            val orders = sampleCollectOrderListItemStateList()

            val base = OrderListScreenContract.State(
                orders = orders,
                isLoading = false,
                isRefreshing = false,
                filters = OrderListScreenContract.State.Filters(
                    showB2B = true,
                    showB2C = true,
                    sortOption = SortOption.TIME_WAITING_DESC
                ),
                isFilterSheetOpen = false,
                selection = SelectionUiState(),
                isDraftBarVisible = false,
                orderCount = orders.size,
                isSubmitting = false,
                submittedCollectOrder = null,
                error = null,
            )

            val withFilters = base.copy(
                filters = base.filters.copy(showB2B = false, showB2C = true),
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
                selection = base.selection.copy(
                    isEnabled = true,
                    selected = setOf(orders[0].invoiceNumber, orders[2].invoiceNumber),
                    isAllSelected = false,
                )
            )

            val multiSelectAll = base.copy(
                selection = base.selection.copy(
                    isEnabled = true,
                    selected = orders.map { it.invoiceNumber }.toSet(),
                    isAllSelected = true,
                )
            )

            return sequenceOf(
                base,
                withFilters,
                multiSelect,
                multiSelectAll,
                loading,
                refreshing,
                error
            )
        }
}

