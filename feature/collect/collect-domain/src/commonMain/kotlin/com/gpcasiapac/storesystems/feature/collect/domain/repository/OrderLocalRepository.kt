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

/**
 * Local (DB-only) repository facade for the Collect feature.
 * Exposes read/write operations backed by Room, plus a write() transaction boundary
 * to allow atomic multi-step writes orchestrated from use cases.
 */
interface OrderLocalRepository {
    /** Transaction boundary for atomic multi-step writes. */
    suspend fun <T> write(block: suspend () -> T): T

    /** Upsert orders into the local DB. Should be called inside write {} when batching. */
    suspend fun upsertOrders(orders: List<CollectOrderWithCustomerWithLineItems>)

    // --- Functions moved from legacy OrderRepository (DB-only concerns) ---
    fun observeOrderCount(): Flow<Int>

    fun getCollectOrderWithCustomerWithLineItemsFlow(invoiceNumber: String): Flow<CollectOrderWithCustomerWithLineItems?>

    fun observeMainOrders(query: MainOrderQuery): Flow<List<CollectOrderWithCustomer>>

    fun observeSearchOrders(query: SearchQuery): Flow<List<CollectOrderWithCustomer>>

    fun getWorkOrderSignatureFlow(workOrderId: WorkOrderId): Flow<Signature?>

    fun getCollectWorkOrderFlow(workOrderId: WorkOrderId): Flow<CollectWorkOrder?>

    fun getWorkOrderWithOrderWithCustomerFlow(workOrderId: WorkOrderId): Flow<WorkOrderWithOrderWithCustomers?>

    fun observeWorkOrderItemsInScanOrder(workOrderId: WorkOrderId): Flow<List<CollectOrderWithCustomer>>

    suspend fun removeWorkOrderItem(workOrderId: WorkOrderId, orderId: String)

    suspend fun deleteWorkOrder(workOrderId: WorkOrderId)

    suspend fun insertOrReplaceSignature(signature: Signature)

    suspend fun setCollectingType(workOrderId: WorkOrderId, type: CollectingType)

    suspend fun setCourierName(workOrderId: WorkOrderId, name: String)

    suspend fun existsInvoice(invoiceNumber: String): Boolean

    suspend fun insertOrReplaceWorkOrder(collectWorkOrder: CollectWorkOrder)

    suspend fun getMaxWorkOrderItemPosition(workOrderId: WorkOrderId): Long

    suspend fun insertWorkOrderItem(item: CollectWorkOrderItem): Boolean

    suspend fun getSearchSuggestions(query: SuggestionQuery): List<SearchSuggestion>

    // New: existence check for work order
    suspend fun workOrderExists(workOrderId: WorkOrderId): Boolean
}
