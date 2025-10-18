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

// Main-list query for filters + sort
data class MainOrderQuery(
    val customerTypes: Set<com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType>,
    val sort: com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption,
)

// Search-only query for debounced search text
data class SearchQuery(
    val text: String,
)

interface OrderRepository {
    /** Observe the total count of orders in the DB (independent of filters/search). */
    fun observeOrderCount(): Flow<Int>

    fun getCollectOrderWithCustomerWithLineItemsFlow(invoiceNumber: String): Flow<CollectOrderWithCustomerWithLineItems?>


    // New reactive streams for main list and search results
    fun observeMainOrders(query: MainOrderQuery): Flow<List<CollectOrderWithCustomer>>

    fun observeSearchOrders(query: SearchQuery): Flow<List<CollectOrderWithCustomer>>

    /**
     * Lightweight, indexed search suggestions from the DB.
     */
    suspend fun getOrderSearchSuggestionList(text: String): List<OrderSearchSuggestion>

    /**
     * Trigger a refresh/sync. In fake repo this seeds demo data.
     */
    suspend fun refreshOrders(): Result<Unit>


    /** Observe the set of selected order IDs for the given user scope. */
    fun getSelectedIdListFlow(userRefId: String): Flow<Set<String>>

    /** Observe the signature (Base64) for the latest open Work Order for the given user. */
    fun observeLatestOpenWorkOrderSignature(userRefId: String): Flow<String?>

    /** Observe the latest open Work Order (without joining orders). */
    fun observeLatestOpenWorkOrder(userRefId: String): Flow<com.gpcasiapac.storesystems.feature.collect.domain.model.CollectWorkOrder?>

    /** Observe the full latest open Work Order with its orders (signature included). */
    fun observeLatestOpenWorkOrderWithOrders(userRefId: String): Flow<com.gpcasiapac.storesystems.feature.collect.domain.model.WorkOrderWithOrderWithCustomers?>

    /** Replace the entire set of selected IDs. */
    suspend fun setSelectedIdList(orderIdList: List<String>, userRefId: String)

    /** Add a single order ID to the selection. */
    suspend fun addSelectedId(orderId: String, userRefId: String)

    /** Remove a single order ID from the selection. */
    suspend fun removeSelectedId(orderId: String, userRefId: String)

    /** Append a batch of order IDs to the current draft (creates draft if missing). */
    suspend fun appendSelectedIds(orderIdList: List<String>, userRefId: String)

    /** Remove a batch of order IDs from the current draft. */
    suspend fun removeSelectedIds(orderIdList: List<String>, userRefId: String)

    /** Clear the selection. */
    suspend fun clear(userRefId: String)

    // Work order lifecycle
    suspend fun createWorkOrder(
        userId: String,
        invoiceNumbers: List<String>
    ): Result<String> // returns workOrderId

    // TODO: Keep for Mutliple open WorkOrders
    suspend fun attachSignatureToWorkOrder(
        workOrderId: String,
        signature: String,        // keep String (compat with your current model)
        signedByName: String?
    ): Result<Unit>

    suspend fun attachSignatureToLatestOpenWorkOrder(
        userRefId: String,
        signature: String,
        signedByName: String?
    ): Result<Unit>

    // New: persist collecting type selection for the latest open Work Order
    suspend fun setCollectingType(userRefId: String, type: com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType): Result<Unit>

    // New: persist courier name for the latest open Work Order
    suspend fun setCourierName(userRefId: String, name: String): Result<Unit>

}
