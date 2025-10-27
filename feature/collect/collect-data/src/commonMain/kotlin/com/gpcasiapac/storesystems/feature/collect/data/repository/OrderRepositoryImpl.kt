package com.gpcasiapac.storesystems.feature.collect.data.repository

import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.feature.collect.data.local.db.AppDatabase
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.CollectOrderDao
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.SignatureDao
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.WorkOrderDao
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderCustomerEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderLineItemEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderItemEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.CollectOrderWithCustomerWithLineItemsRelation
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toDomain
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toEntity
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toRelation
import com.gpcasiapac.storesystems.feature.collect.data.network.dto.CollectOrderDto
import com.gpcasiapac.storesystems.feature.collect.data.network.source.OrderNetworkDataSource
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectWorkOrder
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerNameSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.InvoiceNumberSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.MainOrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.model.PhoneSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.SalesOrderNumberSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.SearchQuery
import com.gpcasiapac.storesystems.feature.collect.domain.model.SearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.Signature
import com.gpcasiapac.storesystems.feature.collect.domain.model.SuggestionKind
import com.gpcasiapac.storesystems.feature.collect.domain.model.SuggestionQuery
import com.gpcasiapac.storesystems.feature.collect.domain.model.WebOrderNumberSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.WorkOrderWithOrderWithCustomers
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class OrderRepositoryImpl(
    private val collectOrderDao: CollectOrderDao,
    private val workOrderDao: WorkOrderDao,
    private val database: AppDatabase,
    private val orderNetworkDataSource: OrderNetworkDataSource,
    private val logger: Logger,
    private val signatureDao: SignatureDao,
) : OrderRepository {


    private val log = logger.withTag("OrderRepository")


    override fun getCollectOrderWithCustomerWithLineItemsFlow(invoiceNumber: String): Flow<CollectOrderWithCustomerWithLineItems?> {
        return collectOrderDao.getCollectOrderWithCustomerWithLineItemsRelationFlow(invoiceNumber = invoiceNumber)
            .map { it.toDomain() }
    }

    override fun getCollectWorkOrderFlow(workOrderId: WorkOrderId): Flow<CollectWorkOrder?> {
        return workOrderDao.getWorkOrderWithOrderWithCustomerRelationFlow(workOrderId = workOrderId)
            .map { relation -> relation?.collectWorkOrderEntity?.toDomain() }
    }

    override fun getWorkOrderWithOrderWithCustomerFlow(workOrderId: WorkOrderId): Flow<WorkOrderWithOrderWithCustomers?> {
        return workOrderDao.getWorkOrderWithOrderWithCustomerRelationFlow(workOrderId = workOrderId)
            .map { it?.toDomain() }
    }

    override fun getWorkOrderSignatureFlow(workOrderId: WorkOrderId): Flow<Signature?> {
        return signatureDao.getSignatureEntityFlow(workOrderId = workOrderId).map { it?.toDomain() }
    }

//    override fun observeLatestOpenWorkOrderWithOrders(userRefId: String): Flow<WorkOrderWithOrderWithCustomers?> {
//        // Simplified: use ordered items relation anchored on work_order_items
//        return workOrderDao.observeLatestOpenWorkOrderForUser(userRefId)
//            .flatMapLatest { relation ->
//                if (relation == null) flowOf(null) else {
//                    val wo = relation.collectWorkOrderEntity
//                    workOrderDao
//                        .observeWorkOrderItemsWithOrders(wo.workOrderId)
//                        .map { itemsWithOrders ->
//                            val orderedDomain =
//                                itemsWithOrders.map { it.orderWithCustomer }.toDomain()
//                            WorkOrderWithOrderWithCustomers(
//                                collectWorkOrder = wo.toDomain(),
//                                collectOrderWithCustomerList = orderedDomain
//                            )
//                        }
//                }
//            }
//    }

    override fun observeWorkOrderItemsInScanOrder(workOrderId: WorkOrderId): Flow<List<CollectOrderWithCustomer>> =
        workOrderDao.observeWorkOrderItemsWithOrders(workOrderId)
            .map { list -> list.map { it.orderWithCustomer }.toDomain() }

    // New: observe main orders via DB-side filter + sort
    override fun observeMainOrders(query: MainOrderQuery): Flow<List<CollectOrderWithCustomer>> {
        val types = query.customerTypes
        if (types.isEmpty()) return flowOf(emptyList())
        val sort = query.sort.name
        return collectOrderDao
            .observeOrdersForMainList(types, sort)
            .map { it.toDomain() }
    }

    // New: observe search orders via DB-side search only
    override fun observeSearchOrders(query: SearchQuery): Flow<List<CollectOrderWithCustomer>> {
        val text = query.text.trim()
        if (text.isEmpty()) return flowOf(emptyList())
        val q = "%${escapeForLike(text)}%"
        return collectOrderDao
            .observeOrdersForSearch(q)
            .map { it.toDomain() }
    }

    override fun observeOrderCount(): Flow<Int> = collectOrderDao.observeCount()

    override suspend fun existsInvoice(invoiceNumber: String): Boolean {
        return collectOrderDao.existsInvoice(invoiceNumber)
    }

    override suspend fun refreshOrders(): Result<Unit> = runCatching {
        log.d { "refreshOrders: start" }
        try {
            val collectOrderDtoList: List<CollectOrderDto> = orderNetworkDataSource.fetchOrders()
            log.d { "refreshOrders: fetched ${collectOrderDtoList.size} orders" }

            val relations: List<CollectOrderWithCustomerWithLineItemsRelation> =
                collectOrderDtoList.toRelation()

            val orderEntities: List<CollectOrderEntity> = relations.map { it.orderEntity }
            val customerEntities: List<CollectOrderCustomerEntity> =
                relations.map { it.customerEntity }
            val lineItemEntities: List<CollectOrderLineItemEntity> =
                relations.flatMap { it.lineItemEntityList }

            database.useWriterConnection { transactor ->
                transactor.immediateTransaction {
                    collectOrderDao.insertOrReplaceCollectOrderEntityList(orderEntities)
                    collectOrderDao.insertOrReplaceCollectOrderCustomerEntityList(customerEntities)
                    collectOrderDao.insertOrReplaceCollectOrderLineItemEntityList(lineItemEntities)
                }
            }
            log.d { "refreshOrders: committed" }
        } catch (t: Throwable) {
            log.e(t) { "refreshOrders: failed" }
            throw t
        }
    }

    override suspend fun insertOrReplaceSignature(signature: Signature) {
        signatureDao.insertOrReplaceSignatureEntity(signature.toEntity())
    }

    override suspend fun insertOrReplaceWorkOrder(collectWorkOrder: CollectWorkOrder) {
        workOrderDao.insertOrReplaceWorkOrderEntity(collectWorkOrder.toEntity())
    }

    override suspend fun setCollectingType(
        workOrderId: WorkOrderId,
        type: CollectingType
    ) {
        workOrderDao.setCollectingType(workOrderId, type)
    }

    override suspend fun setCourierName(workOrderId: WorkOrderId, name: String) {
        workOrderDao.setCourierName(workOrderId, name)
    }

    override suspend fun getSearchSuggestions(query: SuggestionQuery): List<SearchSuggestion> {
        val text = query.text
        val per = query.perKindLimit
        val include = query.includeKinds

        val out = mutableListOf<SearchSuggestion>()

        // Treat whitespace-only as blank to ensure default suggestions are shown
        if (text.isBlank()) {
            if (SuggestionKind.CUSTOMER_NAME in include) {
                collectOrderDao.getAllCustomerNames(limit = query.maxTotal).forEach { row ->
                    val name = row.name.trim()
                    if (name.isNotEmpty()) out += CustomerNameSuggestion(
                        text = name,
                        customerType = row.type,
                    )
                }
            }
            return out
        }

        // Substring match to allow last 4 digits etc. Use trimmed text for LIKE pattern only
        val contains = "%" + escapeForLike(text.trim()) + "%"

        if (SuggestionKind.CUSTOMER_NAME in include) {
            collectOrderDao.getCustomerNameSuggestionsPrefix(contains, per).forEach { row ->
                val name = row.name.trim()
                if (name.isNotEmpty()) out += CustomerNameSuggestion(
                    text = name,
                    customerType = row.type,
                )
            }
        }
        if (SuggestionKind.INVOICE_NUMBER in include) {
            collectOrderDao.getInvoiceSuggestionsPrefix(contains, per).forEach { inv ->
                out += InvoiceNumberSuggestion(inv)
            }
        }
        if (SuggestionKind.WEB_ORDER_NUMBER in include) {
            collectOrderDao.getWebOrderSuggestionsPrefix(contains, per).forEach { web ->
                out += WebOrderNumberSuggestion(web)
            }
        }
        if (SuggestionKind.SALES_ORDER_NUMBER in include) {
            collectOrderDao.getSalesOrderSuggestionsPrefix(contains, per).forEach { so ->
                out += SalesOrderNumberSuggestion(so)
            }
        }
        if (SuggestionKind.PHONE in include) {
            collectOrderDao.getPhoneSuggestionsPrefix(contains, per).forEach { phone ->
                val p = phone.trim()
                if (p.isNotEmpty()) out += PhoneSuggestion(p)
            }
        }

        // De-dupe by (kind + lowercase text)
        val deduped = out.distinctBy { it.kind to it.text.lowercase() }

        // Simple priority ranking
        val rank = mapOf(
            SuggestionKind.CUSTOMER_NAME to 0,
            SuggestionKind.INVOICE_NUMBER to 1,
            SuggestionKind.WEB_ORDER_NUMBER to 2,
            SuggestionKind.SALES_ORDER_NUMBER to 3,
            SuggestionKind.PHONE to 4,
        )
        val sorted = deduped.sortedWith(compareBy({ rank[it.kind] ?: 99 }, { it.text }))
        return if (sorted.size <= query.maxTotal) sorted else sorted.take(query.maxTotal)
    }

// TODO: Replace with WorkOrderId
//    override suspend fun setSelectedIdList(orderIdList: List<String>, userRefId: String) {
//        database.useWriterConnection { transactor ->
//            transactor.immediateTransaction {
//                val existingOpenWorkOrder =
//                    workOrderDao.getCollectWorkOrderEntity(userId = userRefId)
//                if (existingOpenWorkOrder != null) {
//                    workOrderDao.deleteWorkOrder(existingOpenWorkOrder.workOrderId)
//                }
//
//                if (orderIdList.isNotEmpty()) {
//                    createWorkOrderInternal(userRefId, orderIdList)
//                }
//            }
//        }
//    }

// TODO: Replace with WorkOrderId
//    override suspend fun addSelectedId(orderId: String, userRefId: String): Boolean {
//        var inserted = false
//        database.useWriterConnection { transactor ->
//            transactor.immediateTransaction {
//                var openWorkOrder = workOrderDao.getCollectWorkOrderEntity(userId = userRefId)
//                if (openWorkOrder == null) {
//                    val workOrderId = randomUUID()
//                    val now = Clock.System.now()
//                    val newWorkOrder = CollectWorkOrderEntity(
//                        workOrderId = workOrderId,
//                        userId = userRefId,
//                        createdAt = now
//                    )
//                    workOrderDao.insertOrReplaceWorkOrder(newWorkOrder)
//                    openWorkOrder = newWorkOrder
//                }
//                val nextPosition =
//                    workOrderDao.getMaxPosition(WorkOrderId(openWorkOrder.workOrderId)) + 1
//                val rowId = workOrderDao.insertItem(
//                    CollectWorkOrderItemEntity(
//                        workOrderId = WorkOrderId(openWorkOrder.workOrderId),
//                        invoiceNumber = orderId,
//                        position = nextPosition
//                    )
//                )
//                inserted = rowId != -1L
//            }
//        }
//        return inserted
//    }

    override suspend fun addOrderToCollectWorkOrder(
        workOrderId: WorkOrderId,
        orderId: String
    ): Boolean {
        var inserted = false
        database.useWriterConnection { transactor ->
            transactor.immediateTransaction {
                val nextPosition = workOrderDao.getMaxPosition(workOrderId) + 1
                val rowId = workOrderDao.insertItem(
                    CollectWorkOrderItemEntity(
                        workOrderId = workOrderId,
                        invoiceNumber = orderId,
                        position = nextPosition
                    )
                )
                inserted = rowId != -1L
            }
        }
        return inserted
    }


    // TODO: Replace with WorkOrderId
    override suspend fun removeWorkOrderItem(workOrderId: WorkOrderId, orderId: String) {
        database.useWriterConnection { transactor ->
            transactor.immediateTransaction {
                workOrderDao.deleteWorkOrderItem(
                    workOrderId = workOrderId,
                    invoiceNumber = orderId
                )
                val remainingItems =
                    workOrderDao.getWorkOrderItemCount(workOrderId = workOrderId)
                if (remainingItems == 0) {
                    workOrderDao.deleteWorkOrder(workOrderId = workOrderId)
                }
            }
        }
    }

    override suspend fun deleteWorkOrder(workOrderId: WorkOrderId) {
        workOrderDao.deleteWorkOrder(workOrderId)
    }

//    private suspend fun createWorkOrderInternal(
//        userId: String,
//        invoiceNumbers: List<String>
//    ): CollectWorkOrderEntity {
//        val workOrderId = WorkOrderId(randomUUID())
//        val now = Clock.System.now()
//        val workOrder = CollectWorkOrderEntity(
//            workOrderId = workOrderId,
//            userId = userId,
//            createdAt = now
//        )
//        val workOrderItems = invoiceNumbers.mapIndexed { index, invoice ->
//            CollectWorkOrderItemEntity(
//                workOrderId = workOrderId,
//                invoiceNumber = invoice,
//                position = index + 1L
//            )
//        }
//        database.useWriterConnection { transactor ->
//            transactor.immediateTransaction {
//                workOrderDao.insertOrReplaceWorkOrderEntity(workOrder)
//                workOrderDao.insertItems(workOrderItems)
//            }
//        }
//        return workOrder
//    }

    private fun escapeForLike(input: String): String {
        return input
            .replace("!", "!!")
            .replace("%", "!%")
            .replace("_", "!_")
    }
}
