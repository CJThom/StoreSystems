package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import com.gpcasiapac.storesystems.feature.collect.domain.model.Representative
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.hours

private val now get() = Clock.System.now()

private fun sampleOrders(): List<Order> = listOf(
    Order(
        id = "1",
        customerType = CustomerType.B2C,
        accountName = null,
        invoiceNumber = "INV-1001",
        webOrderNumber = "WEB-7771",
        pickedAt = now - 12.minutes,
        customer = com.gpcasiapac.storesystems.feature.collect.domain.model.Customer(
            customerNumber = "CUST-0001",
            customerType = CustomerType.B2C,
            accountName = null,
            firstName = "Jane",
            lastName = "Doe",
            phone = "+65 8123 4567",
        )
    ),
    Order(
        id = "2",
        customerType = CustomerType.B2B,
        accountName = "Acme Corp",
        invoiceNumber = "INV-1002",
        webOrderNumber = null,
        pickedAt = now - 2.hours,
        customer = com.gpcasiapac.storesystems.feature.collect.domain.model.Customer(
            customerNumber = "ACC-0002",
            customerType = CustomerType.B2B,
            accountName = "Acme Corp",
            firstName = null,
            lastName = null,
            phone = "+65 8000 0002",
        )
    ),
    Order(
        id = "3",
        customerType = CustomerType.B2C,
        accountName = null,
        invoiceNumber = "INV-1003",
        webOrderNumber = "WEB-7773",
        pickedAt = now - 1.hours,
        customer = com.gpcasiapac.storesystems.feature.collect.domain.model.Customer(
            customerNumber = "CUST-0003",
            customerType = CustomerType.B2C,
            accountName = null,
            firstName = "John",
            lastName = "Smith",
            phone = "+65 8123 0003",
        )
    ),
)

class OrderDetailScreenStateProvider : PreviewParameterProvider<OrderDetailScreenContract.State> {
    override val values: Sequence<OrderDetailScreenContract.State>
        get() {
            val orders = sampleOrders()

            val base = OrderDetailScreenContract.State(
                // Single order context
                orderId = orders.first().id,
                order = orders.first(),

                // Multi-order context
                orderList = emptyList(),

                // Flags
                isLoading = false,
                error = null,

                // Collecting
                collectingType = CollectingType.STANDARD,

                // Account flow
                representativeSearchText = "",
                recentRepresentativeList = listOf(
                    Representative("rep-1", "John Doe", "#9288180049912"),
                    Representative("rep-2", "Custa Ma", "#9288180049912"),
                    Representative("rep-3", "Alice Smith", "#9288180049912"),
                ),
                selectedRepresentativeIdList = emptySet(),

                // Courier
                courierName = "",

                // Signature
                isSigned = false,

                // Correspondence
                emailChecked = true,
                printChecked = true,
            )

            val singleB2C = base

            val multiB2C = base.copy(
                orderId = null,
                order = null,
                orderList = orders,
                collectingType = CollectingType.STANDARD,
            )

            val accountFlow = base.copy(
                orderId = null,
                order = null,
                orderList = orders.take(2),
                collectingType = CollectingType.ACCOUNT,
                representativeSearchText = "Jo",
                selectedRepresentativeIdList = setOf("rep-1"),
            )

            val courierFlow = base.copy(
                orderId = null,
                order = null,
                orderList = orders,
                collectingType = CollectingType.COURIER,
                courierName = "DHL Express",
            )

            val loading = base.copy(
                isLoading = true,
                order = null,
                orderList = emptyList(),
            )

            val error = base.copy(
                error = "Failed to load order. Please try again.",
            )

            return sequenceOf(singleB2C, multiB2C, accountFlow, courierFlow, loading, error)
        }
}
