package com.gpcasiapac.storesystems.feature.collect.presentation.destination

import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail.model.CollectOrderCustomerState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail.model.CollectOrderLineItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail.model.CollectOrderState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail.model.CollectOrderWithCustomerWithLineItemsState
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
fun sampleCollectOrderListItemStateList(): List<CollectOrderListItemState> = listOf(
    CollectOrderListItemState(
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        customerType = CustomerType.B2C,
        customerName = "Johnathan Citizenship",
        pickedAt = now - 2.days,
    ),
    CollectOrderListItemState(
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        customerType = CustomerType.B2B,
        customerName = "ABC Motorsports PTY Limited",
        pickedAt = now - 2.hours,
    ),
    CollectOrderListItemState(
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        customerType = CustomerType.B2B,
        customerName = "AU Mechanics",
        pickedAt = now - 45.minutes,
    ),
    CollectOrderListItemState(
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        customerType = CustomerType.B2C,
        customerName = "Jane Doe",
        pickedAt = now - 30.minutes,
    ),
    CollectOrderListItemState(
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        customerType = CustomerType.B2C,
        customerName = "John Doe",
        pickedAt = now - 15.minutes,
    ),
    CollectOrderListItemState(
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        customerType = CustomerType.B2C,
        customerName = "Bob Jane",
        pickedAt = now - 5.minutes,
    ),
)

fun sampleCollectOrderWithCustomerWithLineItemsState(): CollectOrderWithCustomerWithLineItemsState = CollectOrderWithCustomerWithLineItemsState(
    order = CollectOrderState(
        invoiceNumber = "10341882849",
        salesOrderNumber = "123456789",
        webOrderNumber = "84777189930",
        createdAt = now - 3.days,
        pickedAt = now - 2.days,
    ),
    customer = CollectOrderCustomerState(
        type = CustomerType.B2C,
        name = "Johnathan Citizenship",
        customerNumber = "123456",
        mobileNumber = "0412345678",
    ),
    lineItemList = listOf(
        CollectOrderLineItemState(
            lineNumber = 1,
        ),
        CollectOrderLineItemState(
            lineNumber = 2,
        ),
        CollectOrderLineItemState(
            lineNumber = 3,
        ),
    )
)
