package com.gpcasiapac.storesystems.feature.collect.domain.repo

import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import kotlinx.coroutines.flow.Flow

/**
 * Query parameters for observing orders. Start minimal: only search text.
 * Extend later with sort, filters, paging, etc.
 */
 data class OrderQuery(
    val searchText: String = "",
)

interface OrderRepository {
    /**
     * Observe orders filtered at the data source level (DB in production, in-memory for fake impl).
     */
    fun getOrderListFlow(query: OrderQuery): Flow<List<Order>>

    /**
     * Trigger a refresh/sync. In fake repo this seeds demo data.
     */
    suspend fun refreshOrders(): Result<Unit>
}
