package com.gpcasiapac.storesystems.feature.collect.presentation.destination

import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model.CollectOrderCustomerState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model.CollectOrderLineItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model.CollectOrderState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model.CollectOrderWithCustomerWithLineItemsState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private val now get() = Clock.System.now()

val sampleLineItemList = listOf(
    CollectOrderLineItemState(
        lineNumber = 1,
        sku = "SKU-001",
        description = "Product 1 Description",
        quantity = 2,
    ),
    CollectOrderLineItemState(
        lineNumber = 2,
        sku = "SKU-002",
        description = "Product 2 Description",
        quantity = 1,
    ),
    CollectOrderLineItemState(
        lineNumber = 3,
        sku = "SKU-003",
        description = "Product 3 Description",
        quantity = 10,
    ),
    CollectOrderLineItemState(
        lineNumber = 4,
        sku = "SKU-003",
        description = "Product 3 Description",
        quantity = 10
    )
)
/**
 * Shared sample data for preview providers.
 * Used by both OrderDetailScreenStateProvider and OrderListScreenStateProvider.
 */
fun sampleCollectOrderListItemStateList(): List<CollectOrderListItemState> = listOf(
    CollectOrderListItemState(
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        customerType = CustomerType.B2C,
        customerName = "Johnathan Citizenship",
        pickedAt = now - 2.days,
    ),
    CollectOrderListItemState(
        invoiceNumber = "10341882850",
        webOrderNumber = "84777189931",
        customerType = CustomerType.B2B,
        customerName = "ABC Motorsports PTY Limited",
        pickedAt = now - 2.hours,
    ),
    CollectOrderListItemState(
        invoiceNumber = "10341882851",
        webOrderNumber = "84777189932",
        customerType = CustomerType.B2B,
        customerName = "AU Mechanics",
        pickedAt = now - 45.minutes,
    ),
    CollectOrderListItemState(
        invoiceNumber = "10341882852",
        webOrderNumber = "84777189933",
        customerType = CustomerType.B2C,
        customerName = "Jane Doe",
        pickedAt = now - 30.minutes,
    ),
    CollectOrderListItemState(
        invoiceNumber = "10341882853",
        webOrderNumber = "84777189934",
        customerType = CustomerType.B2C,
        customerName = "John Doe",
        pickedAt = now - 15.minutes,
    ),
    CollectOrderListItemState(
        invoiceNumber = "10341882854",
        webOrderNumber = "84777189935",
        customerType = CustomerType.B2C,
        customerName = "Bob Jane",
        pickedAt = now - 5.minutes,
    ),
)

fun sampleCollectOrderWithCustomerWithLineItemsState(): CollectOrderWithCustomerWithLineItemsState = CollectOrderWithCustomerWithLineItemsState(
    order = CollectOrderState(
        invoiceNumber = "10341882855",
        orderNumber = "123456789",
        webOrderNumber = "84777189936",
        createdAt = now - 3.days,
        pickedAt = now - 2.days,
    ),
    customer = CollectOrderCustomerState(
        type = CustomerType.B2C,
        name = "Johnathan Citizenship",
        customerNumber = "123456",
        mobileNumber = "0412345678",
    ),
    lineItemList = sampleLineItemList
)
