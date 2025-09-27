package com.gpcasiapac.storesystems.feature.collect.domain.repo.fake

import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import com.gpcasiapac.storesystems.feature.collect.domain.repo.OrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.repo.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOn
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

/**
 * Simple in-memory fake repository that behaves like a DB-backed source of truth.
 * - observeOrders returns a filtered view of an internal MutableStateFlow.
 * - refreshOrders seeds demo data (simulating network/DB write).
 */
class FakeOrderRepository : OrderRepository {
    private val state = MutableStateFlow<List<Order>>(emptyList())

    override fun getOrderListFlow(query: OrderQuery): Flow<List<Order>> =
        state.asStateFlow()
            .map { all ->
                val q = query.searchText.trim().lowercase()
                if (q.isEmpty()) all else all.filter { o ->
                    o.customerName.lowercase().contains(q) ||
                    (o.invoiceNumber.lowercase().contains(q)) ||
                    ((o.webOrderNumber ?: "").lowercase().contains(q))
                }
            }
            .flowOn(Dispatchers.Default)

    override suspend fun refreshOrders(): Result<Unit> = runCatching {
        // Simulate slight delay and seed data
        delay(200)
        val now = Clock.System.now()
        val demo = (1..40).map { idx ->
            Order(
                id = "ORD-$idx",
                customerType = if (idx % 2 == 0) CustomerType.B2B else CustomerType.B2C,
                customerName = if (idx % 2 == 0) "Acme Corp #$idx" else "John Smith #$idx",
                invoiceNumber = "INV-${1000 + idx}",
                webOrderNumber = if (idx % 3 == 0) "WEB-${2000 + idx}" else null,
                pickedAt = now - idx.hours,
            )
        }
        state.value = demo
    }
}
