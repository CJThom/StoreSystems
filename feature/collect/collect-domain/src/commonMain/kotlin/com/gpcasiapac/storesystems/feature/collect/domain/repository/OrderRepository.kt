package com.gpcasiapac.storesystems.feature.collect.domain.repository

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectWorkOrder
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectWorkOrderItem
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.MainOrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.model.SearchQuery
import com.gpcasiapac.storesystems.feature.collect.domain.model.SearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.Signature
import com.gpcasiapac.storesystems.feature.collect.domain.model.SuggestionQuery
import com.gpcasiapac.storesystems.feature.collect.domain.model.WorkOrderWithOrderWithCustomers
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import kotlinx.coroutines.flow.Flow


interface OrderRepository {
//    /** Observe the total count of orders in the DB (independent of filters/search). */
//    fun observeOrderCount(): Flow<Int>
//
//    fun getCollectOrderWithCustomerWithLineItemsFlow(invoiceNumber: String): Flow<CollectOrderWithCustomerWithLineItems?>
//
//
//    // New reactive streams for main list and search results
//    fun observeMainOrders(query: MainOrderQuery): Flow<List<CollectOrderWithCustomer>>
//
//    fun observeSearchOrders(query: SearchQuery): Flow<List<CollectOrderWithCustomer>>
//
//    /**
//     * Lightweight, indexed search suggestions from the DB.
//     */
//    suspend fun getSearchSuggestions(query: SuggestionQuery): List<SearchSuggestion>
//
//
//    /** Observe signature record (image + name + timestamp) for the latest open Work Order. */
//    fun getWorkOrderSignatureFlow(workOrderId: WorkOrderId): Flow<Signature?>
//
//    /** Observe the latest open Work Order (without joining orders). */
//    fun getCollectWorkOrderFlow(workOrderId: WorkOrderId): Flow<CollectWorkOrder?>
//
//    /** Observe the full latest open Work Order with its orders (signature included). */
//    fun getWorkOrderWithOrderWithCustomerFlow(workOrderId: WorkOrderId): Flow<WorkOrderWithOrderWithCustomers?>
//
//    /** New: Observe ordered list of orders (domain) for a given Work Order id. */
//    fun observeWorkOrderItemsInScanOrder(workOrderId: WorkOrderId): Flow<List<CollectOrderWithCustomer>>
//
//    /** Remove a single order ID from the selection. */
//    suspend fun removeWorkOrderItem(workOrderId: WorkOrderId, orderId: String)
//
//    /** Clear the selection. */
//    suspend fun deleteWorkOrder(workOrderId: WorkOrderId)
//
//    suspend fun insertOrReplaceSignature(signature: Signature)
//
//    // New: persist collecting type selection for the latest open Work Order
//    suspend fun setCollectingType(workOrderId: WorkOrderId, type: CollectingType)
//
//    // New: persist courier name for the latest open Work Order
//    suspend fun setCourierName(workOrderId: WorkOrderId, name: String)
//
//    /** Lightweight existence check by invoice number (case-insensitive). */
//    suspend fun existsInvoice(invoiceNumber: String): Boolean
//
//    suspend fun insertOrReplaceWorkOrder(collectWorkOrder: CollectWorkOrder)
//
//    // Split: compute next position in the use case, then insert prepared item
//    suspend fun getMaxWorkOrderItemPosition(workOrderId: WorkOrderId): Long
//
//    suspend fun insertWorkOrderItem(item: CollectWorkOrderItem): Boolean
//
//    // Transaction boundary for atomic multi-step writes. All repository calls inside this block
//    // will run within a single writer transaction.
//    suspend fun <T> write(block: suspend () -> T): T

}
