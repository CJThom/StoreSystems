package com.gpcasiapac.storesystems.feature.collect.data.network.source

import com.gpcasiapac.storesystems.feature.collect.data.network.dto.OrderDto
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

/**
 * Mock implementation that returns a deterministic list of orders.
 * This temporarily holds the fake data until a real backend is wired.
 */
class MockOrderNetworkDataSource : OrderNetworkDataSource {
    override suspend fun fetchOrders(): List<OrderDto> {
        val now = Clock.System.now()
        return (1..40).map { idx ->
            OrderDto(
                id = "ORD-$idx",
                customerType = if (idx % 2 == 0) "B2B" else "B2C",
                customerName = if (idx % 2 == 0) "Acme Corp #$idx" else "John Smith #$idx",
                invoiceNumber = "INV-${1000 + idx}",
                webOrderNumber = if (idx % 3 == 0) "WEB-${2000 + idx}" else null,
                pickedAtEpochMillis = (now - idx.hours).toEpochMilliseconds(),
            )
        }
    }
}
