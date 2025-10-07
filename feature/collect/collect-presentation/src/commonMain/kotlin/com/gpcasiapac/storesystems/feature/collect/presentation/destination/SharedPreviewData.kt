package com.gpcasiapac.storesystems.feature.collect.presentation.destination

import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private val now get() = Clock.System.now()

/**
 * Shared sample data for preview providers.
 * Used by both OrderDetailScreenStateProvider and OrderListScreenStateProvider.
 */
fun sampleCollectOrderListItemStates(): List<CollectOrderListItemState> = listOf(
    CollectOrderListItemState(
        id = "1",
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        customerType = CustomerType.B2C,
        customerName = "Johnathan Citizenship",
        pickedAt = now - 2.days,
    ),
    CollectOrderListItemState(
        id = "2",
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        customerType = CustomerType.B2B,
        customerName = "ABC Motorsports PTY Limited",
        pickedAt = now - 2.hours,
    ),
    CollectOrderListItemState(
        id = "3",
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        customerType = CustomerType.B2B,
        customerName = "AU Mechanics",
        pickedAt = now - 45.minutes,
    ),
    CollectOrderListItemState(
        id = "4",
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        customerType = CustomerType.B2C,
        customerName = "Jane Doe",
        pickedAt = now - 30.minutes,
    ),
    CollectOrderListItemState(
        id = "5",
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        customerType = CustomerType.B2C,
        customerName = "John Doe",
        pickedAt = now - 15.minutes,
    ),
    CollectOrderListItemState(
        id = "6",
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        customerType = CustomerType.B2C,
        customerName = "Bob Jane",
        pickedAt = now - 5.minutes,
    ),
)

fun sampleCollectOrderState(): CollectOrderWithCustomerWithLineItemsState = CollectOrderWithCustomerWithLineItemsState(
    id = "1",
    invoiceNumber = "10341882849",
    salesOrderNumber = "123456789",
    webOrderNumber = "84777189930",
    createdAt = now - 3.days,
    pickedAt = now - 2.days,
    customerType = CustomerType.B2C,
    customerName = "Johnathan Citizenship",
    customerNumber = "123456",
    customerMobileNumber = "0412345678",
    productList = listOf(
        "Product A",
        "Product B",
        "Product C",
    )
)
