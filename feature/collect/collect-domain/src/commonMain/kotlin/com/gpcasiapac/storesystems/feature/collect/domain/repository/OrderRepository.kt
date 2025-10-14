package com.gpcasiapac.storesystems.feature.collect.domain.repository

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.WorkOrderSummary
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

    fun getCollectOrderWithCustomerListFlow(invoiceNumbers: Set<String>): Flow<List<CollectOrderWithCustomer>>


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

    suspend fun attachSignatureToWorkOrder(
        workOrderId: String,
        signature: String,        // keep String (compat with your current model)
        signedByName: String?
    ): Result<Unit>

    // TODO: remove?
    fun observeMyOpenWorkOrders(userRefId: String): Flow<List<WorkOrderSummary>>

    suspend fun submitWorkOrder(workOrderId: String): Result<Unit>

    // Progress/persistence helpers for Save/Discard behavior
    /** True if the draft has progress worth keeping (multi-selection or signature). */
    suspend fun hasProgress(workOrderId: String): Boolean

    /** Observe progress state for a draft; useful to decide showing Save/Discard. */
    fun observeHasProgress(workOrderId: String): Flow<Boolean>

    /** Delete the draft if no progress; returns true if deleted. */
    suspend fun discardIfNoProgress(workOrderId: String): Boolean

    /** Delete a draft explicitly (Discard choice). */
    suspend fun clearWorkOrderById(workOrderId: String)
}
