package com.gpcasiapac.storesystems.feature.collect.domain.repository

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectWorkOrder
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.SearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.domain.model.WorkOrderWithOrderWithCustomers
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
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
    val customerTypes: Set<CustomerType>,
    val sort: SortOption,
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
    suspend fun getSearchSuggestions(query: SuggestionQuery): List<SearchSuggestion>

    /**
     * Trigger a refresh/sync. In fake repo this seeds demo data.
     */
    suspend fun refreshOrders(): Result<Unit>

    /** Observe the latest open Work Order (without joining orders). */
    fun observeLatestOpenWorkOrder(workOrderId: WorkOrderId): Flow<CollectWorkOrder?>

    /** Observe the full latest open Work Order with its orders (signature included). */
    fun getWorkOrderWithOrderWithCustomerFlow(workOrderId: WorkOrderId): Flow<WorkOrderWithOrderWithCustomers?>

    /** New: Observe ordered list of orders (domain) for a given Work Order id. */
    fun observeWorkOrderItemsInScanOrder(workOrderId: WorkOrderId): Flow<List<CollectOrderWithCustomer>>

    /** Replace the entire set of selected IDs. */
    suspend fun setSelectedIdList(orderIdList: List<String>, userRefId: String)

    /** Add a single order ID to the selection. Returns true if newly added, false if it already existed. */
    suspend fun addSelectedId(orderId: String, userRefId: String): Boolean

    /** Remove a single order ID from the selection. */
    suspend fun removeSelectedId(orderId: String, userRefId: String)

    /** Clear the selection. */
    suspend fun clear(workOrderId: WorkOrderId)

    suspend fun attachSignature(
        workOrderId: WorkOrderId,
        signature: String,
        signedByName: String?
    ): Result<Unit>

    // New: persist collecting type selection for the latest open Work Order
    suspend fun setCollectingType(workOrderId: WorkOrderId, type: CollectingType)

    // New: persist courier name for the latest open Work Order
    suspend fun setCourierName(workOrderId: WorkOrderId, name: String)

    /** Lightweight existence check by invoice number (case-insensitive). */
    suspend fun existsInvoice(invoiceNumber: String): Boolean

    suspend fun insertOrReplaceWorkOrder(collectWorkOrder: CollectWorkOrder)

}
