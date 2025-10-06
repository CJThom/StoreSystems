package com.gpcasiapac.storesystems.feature.collect.presentation.destination

import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderState
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private val now get() = Clock.System.now()

/**
 * Shared sample data for preview providers.
 * Used by both OrderDetailScreenStateProvider and OrderListScreenStateProvider.
 */
fun sampleCollectOrderStates(): List<CollectOrderState> = listOf(
    CollectOrderState(
        id = "1",
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        customerType = CustomerType.B2C,
        customerName = "Johnathan Citizenship",
        pickedAt = now - 2.days,
    ),
    CollectOrderState(
        id = "2",
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        customerType = CustomerType.B2B,
        customerName = "ABC Motorsports PTY Limited",
        pickedAt = now - 2.hours,
    ),
    CollectOrderState(
        id = "3",
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        customerType = CustomerType.B2B,
        customerName = "AU Mechanics",
        pickedAt = now - 45.minutes,
    ),
    CollectOrderState(
        id = "4",
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        customerType = CustomerType.B2C,
        customerName = "Jane Doe",
        pickedAt = now - 30.minutes,
    ),
    CollectOrderState(
        id = "5",
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        customerType = CustomerType.B2C,
        customerName = "John Doe",
        pickedAt = now - 15.minutes,
    ),
    CollectOrderState(
        id = "6",
        invoiceNumber = "10341882849",
        webOrderNumber = "84777189930",
        customerType = CustomerType.B2C,
        customerName = "Bob Jane",
        pickedAt = now - 5.minutes,
    ),
)
