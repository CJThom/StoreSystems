package com.gpcasiapac.storesystems.feature.collect.domain.repository

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestion
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
    fun getCollectOrderWithCustomerWithLineItemsListFlow(orderQuery: OrderQuery): Flow<List<CollectOrderWithCustomerWithLineItems>>

    fun getCollectOrderWithCustomerWithLineItemsFlow(invoiceNumber: String): Flow<CollectOrderWithCustomerWithLineItems?>

    fun getCollectOrderWithCustomerListFlow(): Flow<List<CollectOrderWithCustomer>>

    /**
     * Lightweight, indexed search suggestions from the DB.
     */
    suspend fun getOrderSearchSuggestionList(text: String): List<OrderSearchSuggestion>

    /**
     * Trigger a refresh/sync. In fake repo this seeds demo data.
     */
    suspend fun refreshOrders(): Result<Unit>

    suspend fun saveSignature(signature: String, invoiceNumber: List<String>): Result<Unit>

    /** Observe the set of selected order IDs for the given user scope. */
    fun getSelectedIdListFlow(userRefId: String): Flow<Set<String>>

    /** Replace the entire set of selected IDs. */
    suspend fun setSelectedIdList(orderIdList: List<String>, userRefId: String)

    /** Add a single order ID to the selection. */
    suspend fun addSelectedId(orderId: String, userRefId: String)

    /** Remove a single order ID from the selection. */
    suspend fun removeSelectedId(orderId: String, userRefId: String)

    /** Clear the selection. */
    suspend fun clear(userRefId: String)
}
