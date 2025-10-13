package com.gpcasiapac.storesystems.feature.collect.data.repository

import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import com.gpcasiapac.storesystems.common.kotlin.util.StringUtils
import com.gpcasiapac.storesystems.feature.collect.data.local.db.AppDatabase
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.CollectOrderDao
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.WorkOrderDao
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderCustomerEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderLineItemEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderItemEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.CollectOrderWithCustomerWithLineItemsRelation
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toDomain
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toRelation
import com.gpcasiapac.storesystems.feature.collect.data.network.dto.CollectOrderDto
import com.gpcasiapac.storesystems.feature.collect.data.network.source.OrderNetworkDataSource
import com.gpcasiapac.storesystems.feature.collect.data.util.randomUUID
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.WorkOrderSummary
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class OrderRepositoryImpl(
    private val collectOrderDao: CollectOrderDao,
    private val workOrderDao: WorkOrderDao,
    private val database: AppDatabase,
    private val orderNetworkDataSource: OrderNetworkDataSource,
) : OrderRepository {

    override fun getCollectOrderWithCustomerWithLineItemsListFlow(orderQuery: OrderQuery): Flow<List<CollectOrderWithCustomerWithLineItems>> {
        return collectOrderDao.getCollectOrderWithCustomerWithLineItemsRelationListFlow()
            .map { orderEntityList ->
                val collectOrderList: List<CollectOrderWithCustomerWithLineItems> =
                    orderEntityList.toDomain()
                val query = orderQuery.searchText.trim().lowercase()
                if (query.isEmpty()) collectOrderList else collectOrderList.filter { o ->
                    val name = if (o.customer.customerType == CustomerType.B2B) {
                        o.customer.accountName.orEmpty()
                    } else StringUtils.fullName(o.customer.firstName, o.customer.lastName)
                    name.lowercase().contains(query) ||
                            o.order.invoiceNumber.lowercase().contains(query) ||
                            ((o.order.webOrderNumber ?: "").lowercase().contains(query))
                }
            }
    }

    override fun getCollectOrderWithCustomerWithLineItemsFlow(invoiceNumber: String): Flow<CollectOrderWithCustomerWithLineItems?> {
        return collectOrderDao.getCollectOrderWithCustomerWithLineItemsRelationFlow(invoiceNumber = invoiceNumber)
            .map { it.toDomain() }
    }

    override fun getCollectOrderWithCustomerListFlow(): Flow<List<CollectOrderWithCustomer>> {
        return collectOrderDao.getCollectOrderWithCustomerRelationListFlow().map { it.toDomain() }
    }

    override fun getCollectOrderWithCustomerListFlow(invoiceNumbers: Set<String>): Flow<List<CollectOrderWithCustomer>> {
        val flow = if (invoiceNumbers.isEmpty()) {
            collectOrderDao.getCollectOrderWithCustomerRelationListFlow()
        } else {
            collectOrderDao.getCollectOrderWithCustomerRelationListFlow(invoiceNumbers)
        }
        return flow.map { it.toDomain() }
    }


    override suspend fun refreshOrders(): Result<Unit> = runCatching {
        val collectOrderDtoList: List<CollectOrderDto> = orderNetworkDataSource.fetchOrders()
        val collectOrderWithCustomerWithLineItemsRelation: List<CollectOrderWithCustomerWithLineItemsRelation> =
            collectOrderDtoList.toRelation()

        val collectOrderEntityList: List<CollectOrderEntity> =
            collectOrderWithCustomerWithLineItemsRelation.map {
                it.orderEntity
            }
        val collectOrderCustomerEntity: List<CollectOrderCustomerEntity> =
            collectOrderWithCustomerWithLineItemsRelation.map {
                it.customerEntity
            }
        val collectOrderLineItemEntityList: List<CollectOrderLineItemEntity> =
            collectOrderWithCustomerWithLineItemsRelation.map {
                it.lineItemEntityList
            }.flatten()

        database.useWriterConnection { transactor ->
            transactor.immediateTransaction {
                collectOrderDao.insertOrReplaceCollectOrderEntityList(collectOrderEntityList)
                collectOrderDao.insertOrReplaceCollectOrderCustomerEntityList(
                    collectOrderCustomerEntity
                )
                collectOrderDao.insertOrReplaceCollectOrderLineItemEntityList(
                    collectOrderLineItemEntityList
                )
            }
        }
    }


    override suspend fun saveSignature(
        signature: String,
        invoiceNumber: List<String>
    ): Result<Unit> = runCatching {
        collectOrderDao.updateSignature(signature, invoiceNumber)
    }

    override suspend fun getOrderSearchSuggestionList(text: String): List<OrderSearchSuggestion> {
        return emptyList() // Original implementation was commented out, restoring to empty list.
    }

    override fun getSelectedIdListFlow(userRefId: String): Flow<Set<String>> {
        return workOrderDao.observeLatestOpenWorkOrderForUser(userRefId)
            .map { workOrderWithOrders ->
                workOrderWithOrders?.orders?.map { it.invoiceNumber }?.toSet() ?: emptySet()
            }
    }

    override suspend fun setSelectedIdList(orderIdList: List<String>, userRefId: String) {
        database.useWriterConnection { transactor ->
            transactor.immediateTransaction {
                val existingOpenWorkOrder = workOrderDao.getOpenWorkOrderForUser(userRefId)
                if (existingOpenWorkOrder != null) {
                    workOrderDao.deleteWorkOrder(existingOpenWorkOrder.workOrderId)
                }

                if (orderIdList.isNotEmpty()) {
                    createWorkOrderInternal(userRefId, orderIdList)
                }
            }
        }
    }

    override suspend fun addSelectedId(orderId: String, userRefId: String) {
        var openWorkOrder = workOrderDao.getOpenWorkOrderForUser(userRefId)
        if (openWorkOrder == null) {
            openWorkOrder = createWorkOrderInternal(userRefId, emptyList())
        }

        workOrderDao.insertItem(
            CollectWorkOrderItemEntity(
                workOrderId = openWorkOrder.workOrderId,
                invoiceNumber = orderId
            )
        )
    }

    override suspend fun removeSelectedId(orderId: String, userRefId: String) {
        val openWorkOrder = workOrderDao.getOpenWorkOrderForUser(userRefId)
        if (openWorkOrder != null) {
            database.useWriterConnection { transactor ->
                transactor.immediateTransaction {
                    workOrderDao.deleteWorkOrderItem(openWorkOrder.workOrderId, orderId)
                    val remainingItems =
                        workOrderDao.getWorkOrderItemCount(openWorkOrder.workOrderId)
                    if (remainingItems == 0) {
                        workOrderDao.deleteWorkOrder(openWorkOrder.workOrderId)
                    }
                }
            }
        }
    }

    override suspend fun clear(userRefId: String) {
        val openWorkOrder = workOrderDao.getOpenWorkOrderForUser(userRefId)
        if (openWorkOrder != null) {
            workOrderDao.deleteWorkOrder(openWorkOrder.workOrderId)
        }
    }

    override suspend fun createWorkOrder(
        userId: String,
        invoiceNumbers: List<String>
    ): Result<String> = runCatching {
        val workOrder = createWorkOrderInternal(userId, invoiceNumbers)
        workOrder.workOrderId
    }

    private suspend fun createWorkOrderInternal(
        userId: String,
        invoiceNumbers: List<String>
    ): CollectWorkOrderEntity {
        val workOrderId = randomUUID()
        val now = Clock.System.now()
        val workOrder = CollectWorkOrderEntity(
            workOrderId = workOrderId,
            userId = userId,
            createdAt = now,
            submittedAt = null,
            signature = null,
            signedAt = null,
            signedByName = null
        )
        val workOrderItems = invoiceNumbers.map { invoice ->
            CollectWorkOrderItemEntity(
                workOrderId = workOrderId,
                invoiceNumber = invoice
            )
        }
        database.useWriterConnection { transactor ->
            transactor.immediateTransaction {
                workOrderDao.insertWorkOrder(workOrder)
                workOrderDao.insertItems(workOrderItems)
            }
        }
        return workOrder
    }


    override suspend fun attachSignatureToWorkOrder(
        workOrderId: String,
        signature: String,
        signedByName: String?
    ): Result<Unit> = runCatching {
        val now = Clock.System.now()
        database.useWriterConnection { transactor ->
            transactor.immediateTransaction {
                workOrderDao.attachSignature(workOrderId, signature, now, signedByName)

                // Compatibility
                val workOrderWithOrders = workOrderDao.getWorkOrder(workOrderId)
                val invoiceNumbers = workOrderWithOrders?.orders?.map { it.invoiceNumber }
                if (!invoiceNumbers.isNullOrEmpty()) {
                    collectOrderDao.updateSignature(signature, invoiceNumbers)
                }
            }
        }
    }

    override fun observeMyOpenWorkOrders(userRefId: String): Flow<List<WorkOrderSummary>> {
        return workOrderDao.observeOpenWorkOrdersForUser(userRefId).map { list ->
            list.map {
                WorkOrderSummary(
                    id = it.workOrder.workOrderId,
                    status = it.workOrder.status,
                    createdAt = it.workOrder.createdAt,
                    orderCount = it.orders.size
                )
            }
        }
    }

    override suspend fun submitWorkOrder(workOrderId: String): Result<Unit> =
        runCatching {
            val now = Clock.System.now()
            workOrderDao.markSubmitted(workOrderId, now)
        }
}
