package com.gpcasiapac.storesystems.feature.collect.data.repository

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
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

/** Thin facade retained for backward compatibility. Delegates to OrderLocalRepository. */
class OrderRepositoryImpl(
    private val local: OrderLocalRepository,
) : OrderRepository {

//    override suspend fun <T> write(block: suspend () -> T): T = local.write(block)
//
//    override fun observeOrderCount(): Flow<Int> = local.observeOrderCount()
//
//    override fun getCollectOrderWithCustomerWithLineItemsFlow(invoiceNumber: String): Flow<CollectOrderWithCustomerWithLineItems?> =
//        local.getCollectOrderWithCustomerWithLineItemsFlow(invoiceNumber)
//
//    override fun observeMainOrders(query: MainOrderQuery): Flow<List<CollectOrderWithCustomer>> =
//        local.observeMainOrders(query)
//
//    override fun observeSearchOrders(query: SearchQuery): Flow<List<CollectOrderWithCustomer>> =
//        local.observeSearchOrders(query)
//
//    override fun getWorkOrderSignatureFlow(workOrderId: WorkOrderId): Flow<Signature?> =
//        local.getWorkOrderSignatureFlow(workOrderId)
//
//    override fun getCollectWorkOrderFlow(workOrderId: WorkOrderId): Flow<CollectWorkOrder?> =
//        local.getCollectWorkOrderFlow(workOrderId)
//
//    override fun getWorkOrderWithOrderWithCustomerFlow(workOrderId: WorkOrderId): Flow<WorkOrderWithOrderWithCustomers?> =
//        local.getWorkOrderWithOrderWithCustomerFlow(workOrderId)
//
//    override fun observeWorkOrderItemsInScanOrder(workOrderId: WorkOrderId): Flow<List<CollectOrderWithCustomer>> =
//        local.observeWorkOrderItemsInScanOrder(workOrderId)
//
//    override suspend fun removeWorkOrderItem(workOrderId: WorkOrderId, orderId: String) =
//        local.removeWorkOrderItem(workOrderId, orderId)
//
//    override suspend fun deleteWorkOrder(workOrderId: WorkOrderId) =
//        local.deleteWorkOrder(workOrderId)
//
//    override suspend fun insertOrReplaceSignature(signature: Signature) =
//        local.insertOrReplaceSignature(signature)
//
//    override suspend fun setCollectingType(workOrderId: WorkOrderId, type: CollectingType) =
//        local.setCollectingType(workOrderId, type)
//
//    override suspend fun setCourierName(workOrderId: WorkOrderId, name: String) =
//        local.setCourierName(workOrderId, name)
//
//    override suspend fun existsInvoice(invoiceNumber: String): Boolean =
//        local.existsInvoice(invoiceNumber)
//
//    override suspend fun insertOrReplaceWorkOrder(collectWorkOrder: CollectWorkOrder) =
//        local.insertOrReplaceWorkOrder(collectWorkOrder)
//
//    override suspend fun getMaxWorkOrderItemPosition(workOrderId: WorkOrderId): Long =
//        local.getMaxWorkOrderItemPosition(workOrderId)
//
//    override suspend fun insertWorkOrderItem(item: CollectWorkOrderItem): Boolean =
//        local.insertWorkOrderItem(item)
//
//    override suspend fun getSearchSuggestions(query: SuggestionQuery): List<SearchSuggestion> =
//        local.getSearchSuggestions(query)
}


