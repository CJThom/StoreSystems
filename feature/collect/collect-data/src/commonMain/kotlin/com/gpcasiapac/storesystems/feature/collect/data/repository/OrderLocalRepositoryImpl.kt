package com.gpcasiapac.storesystems.feature.collect.data.repository

import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.data.local.db.AppDatabase
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.CollectOrderDao
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.SignatureDao
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.WorkOrderDao
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toDomain
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toEntity
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toEntityTriples
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectWorkOrder
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectWorkOrderItem
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerNameSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
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
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

/** DB-only implementation backing OrderLocalRepository. */
class OrderLocalRepositoryImpl(
    private val database: AppDatabase,
    private val collectOrderDao: CollectOrderDao,
    private val workOrderDao: WorkOrderDao,
    private val signatureDao: SignatureDao,
) : OrderLocalRepository {

    override suspend fun <T> write(block: suspend () -> T): T {
        var result: T? = null
        database.useWriterConnection { tx ->
            tx.immediateTransaction {
                result = block()
            }
        }
        @Suppress("UNCHECKED_CAST")
        return result as T
    }

    override suspend fun upsertOrders(orders: List<CollectOrderWithCustomerWithLineItems>) {
        if (orders.isEmpty()) return
        val (orderEntities, customerEntities, lineItemEntities) = orders.toEntityTriples()
        collectOrderDao.insertOrReplaceCollectOrderEntityList(orderEntities)
        collectOrderDao.insertOrReplaceCollectOrderCustomerEntityList(customerEntities)
        collectOrderDao.insertOrReplaceCollectOrderLineItemEntityList(lineItemEntities)
    }

    override fun observeOrderCount(): Flow<Int> = collectOrderDao.observeCount()

    override fun getCollectOrderWithCustomerWithLineItemsFlow(invoiceNumber: InvoiceNumber): Flow<CollectOrderWithCustomerWithLineItems?> {
        return collectOrderDao.getCollectOrderWithCustomerWithLineItemsRelationFlow(invoiceNumber)
            .map { it.toDomain() }
    }

    override fun observeMainOrders(query: MainOrderQuery): Flow<List<CollectOrderWithCustomer>> {
        val types = query.customerTypes
        if (types.isEmpty()) return flowOf(emptyList())
        val sort = query.sort.name
        return collectOrderDao.observeOrdersForMainList(sort).map { it.toDomain() }
    }

    override fun observeSearchOrders(query: SearchQuery): Flow<List<CollectOrderWithCustomer>> {
        val text = query.text.trim()
        val like: String? = if (text.isEmpty()) null else "%${escapeForLike(text)}%"
        return flow {
            val scope: Set<String> = resolveInvoiceScope(query.selected)
            when {
                like == null && scope.isEmpty() -> emit(emptyList())
                scope.isEmpty() -> emitAll(collectOrderDao.observeOrdersForSearch(like!!).map { it.toDomain() })
                else -> emitAll(collectOrderDao.observeOrdersForSearchScoped(like, scope).map { it.toDomain() })
            }
        }
    }

    override fun getWorkOrderSignatureFlow(workOrderId: WorkOrderId): Flow<Signature?> {
        return signatureDao.getSignatureEntityFlow(workOrderId).map { it?.toDomain() }
    }

    override fun getCollectWorkOrderFlow(workOrderId: WorkOrderId): Flow<CollectWorkOrder?> {
        return workOrderDao.getWorkOrderWithOrderWithCustomerRelationFlow(workOrderId)
            .map { relation -> relation?.collectWorkOrderEntity?.toDomain() }
    }

    override fun getWorkOrderWithOrderWithCustomerFlow(workOrderId: WorkOrderId): Flow<WorkOrderWithOrderWithCustomers?> {
        return workOrderDao.getWorkOrderWithOrderWithCustomerRelationFlow(workOrderId)
            .map { it?.toDomain() }
    }

    override fun observeWorkOrderItemsInScanOrder(workOrderId: WorkOrderId): Flow<List<CollectOrderWithCustomer>> =
        workOrderDao.observeWorkOrderItemsWithOrders(workOrderId)
            .map { list -> list.map { it.orderWithCustomer }.toDomain() }

    override fun getCollectOrderWithCustomerWithLineItemsListFlow(workOrderId: WorkOrderId): Flow<List<CollectOrderWithCustomerWithLineItems>> {
        return workOrderDao.getWorkOrderItemWithOrderWithCustomerWithLineItemsRelationListFlow(
            workOrderId
        ).map { list ->
            list.map { it.collectOrderWithCustomerWithLineItemsRelation }.toDomain()
        }
    }

    override suspend fun deleteWorkOrderItems(
        workOrderId: WorkOrderId,
        invoiceNumberList: List<InvoiceNumber>
    ) {
        if (invoiceNumberList.isEmpty()) return
        workOrderDao.deleteItemsForWorkOrder(
            workOrderId = workOrderId,
            invoiceNumberList = invoiceNumberList
        )
    }

    override suspend fun getWorkOrderItemCount(workOrderId: WorkOrderId): Int =
        workOrderDao.getWorkOrderItemCount(workOrderId)

    override suspend fun deleteWorkOrder(workOrderId: WorkOrderId) {
        workOrderDao.deleteWorkOrder(workOrderId)
    }

    override suspend fun insertOrReplaceSignature(signature: Signature) {
        signatureDao.insertOrReplaceSignatureEntity(signature.toEntity())
    }

    override suspend fun setCollectingType(workOrderId: WorkOrderId, type: CollectingType) {
        workOrderDao.setCollectingType(workOrderId, type)
    }

    override suspend fun setCourierName(workOrderId: WorkOrderId, name: String) {
        workOrderDao.setCourierName(workOrderId, name)
    }

    override suspend fun existsInvoice(invoiceNumber: String): Boolean {
        return collectOrderDao.existsInvoice(invoiceNumber)
    }

    override suspend fun insertOrReplaceWorkOrder(collectWorkOrder: CollectWorkOrder) {
        workOrderDao.insertOrReplaceWorkOrderEntity(collectWorkOrder.toEntity())
    }

    override suspend fun getMaxWorkOrderItemPosition(workOrderId: WorkOrderId): Long {
        return workOrderDao.getMaxPosition(workOrderId)
    }

    override suspend fun insertWorkOrderItem(workOrderItem: CollectWorkOrderItem): Long {
        return workOrderDao.insertOrIgnoreWorkOrderItem(workOrderItem.toEntity())
    }

    override suspend fun insertWorkOrderItemList(workOrderItemList: List<CollectWorkOrderItem>): List<Long> {
        return workOrderDao.insertOrIgnoreWorkOrderItemList(workOrderItemList.toEntity())
    }

    override suspend fun getSearchSuggestions(query: SuggestionQuery): List<SearchSuggestion> {
        val text = query.text.trim()
        val per = query.perKindLimit
        val include = query.includeKinds
        val selected = query.selected

        // Compute invoice scope from selected chips (intersection)
        val scope: Set<String> = resolveInvoiceScope(selected)

        // Blank text → show default top customers (scoped when chips present)
        if (text.isBlank()) {
            if (SuggestionKind.CUSTOMER_NAME !in include) return emptyList()
            val rows = if (scope.isEmpty()) {
                collectOrderDao.getTopCustomers(limit = query.maxTotal)
            } else {
                collectOrderDao.getTopCustomersScoped(invoiceScope = scope, limit = query.maxTotal)
            }
            return rows.map { row ->
                CustomerNameSuggestion(text = row.name.trim(), customerType = row.customerType)
            }
        }

        val prefix = "%" + escapeForLike(text) + "%"
        val out = mutableListOf<SearchSuggestion>()

        if (SuggestionKind.CUSTOMER_NAME in include) {
            if (scope.isEmpty()) {
                collectOrderDao.getCustomerNameSuggestionsPrefixWithType(prefix, per).forEach { row ->
                    val name = row.name.trim()
                    if (name.isNotEmpty()) out += CustomerNameSuggestion(name, row.customerType)
                }
            } else {
                collectOrderDao.getCustomerNameSuggestionsPrefixScoped(prefix, scope, per).forEach { row ->
                    val name = row.name.trim()
                    if (name.isNotEmpty()) out += CustomerNameSuggestion(name, row.customerType)
                }
            }
        }
        if (SuggestionKind.INVOICE_NUMBER in include) {
            if (scope.isEmpty()) {
                collectOrderDao.getInvoiceSuggestionsPrefix(prefix, per).forEach { inv ->
                    out += InvoiceNumberSuggestion(inv)
                }
            } else {
                collectOrderDao.getInvoiceSuggestionsPrefixScoped(prefix, scope, per).forEach { inv ->
                    out += InvoiceNumberSuggestion(inv)
                }
            }
        }
        if (SuggestionKind.WEB_ORDER_NUMBER in include) {
            if (scope.isEmpty()) {
                collectOrderDao.getWebOrderSuggestionsPrefix(prefix, per).forEach { web ->
                    out += WebOrderNumberSuggestion(web)
                }
            } else {
                collectOrderDao.getWebOrderSuggestionsPrefixScoped(prefix, scope, per).forEach { web ->
                    out += WebOrderNumberSuggestion(web)
                }
            }
        }
        if (SuggestionKind.SALES_ORDER_NUMBER in include) {
            if (scope.isEmpty()) {
                collectOrderDao.getSalesOrderSuggestionsPrefix(prefix, per).forEach { so ->
                    out += SalesOrderNumberSuggestion(so)
                }
            } else {
                collectOrderDao.getSalesOrderSuggestionsPrefixScoped(prefix, scope, per).forEach { so ->
                    out += SalesOrderNumberSuggestion(so)
                }
            }
        }
        if (SuggestionKind.PHONE in include) {
            if (scope.isEmpty()) {
                collectOrderDao.getPhoneSuggestionsPrefix(prefix, per).forEach { phone ->
                    val p = phone.trim()
                    if (p.isNotEmpty()) out += PhoneSuggestion(p)
                }
            } else {
                collectOrderDao.getPhoneSuggestionsPrefixScoped(prefix, scope, per).forEach { phone ->
                    val p = phone.trim()
                    if (p.isNotEmpty()) out += PhoneSuggestion(p)
                }
            }
        }

        val rank = mapOf(
            SuggestionKind.CUSTOMER_NAME to 0,
            SuggestionKind.INVOICE_NUMBER to 1,
            SuggestionKind.WEB_ORDER_NUMBER to 2,
            SuggestionKind.SALES_ORDER_NUMBER to 3,
            SuggestionKind.PHONE to 4,
        )
        return out
            .filter { it.text.isNotBlank() }
            .distinctBy { it.kind to it.text.lowercase() }
            .sortedWith(compareBy({ rank[it.kind] ?: 99 }, { it.text }))
            .take(query.maxTotal)
    }

    private suspend fun resolveInvoiceScope(chips: List<SearchSuggestion>): Set<String> {
        if (chips.isEmpty()) return emptySet()
        val perChip: List<Set<String>> = chips.map { chip ->
            when (chip) {
                is CustomerNameSuggestion -> collectOrderDao.getInvoiceNumbersByCustomerName(chip.text).toSet()
                is InvoiceNumberSuggestion -> setOf(chip.text)
                is WebOrderNumberSuggestion -> collectOrderDao.getInvoiceNumbersByWebOrder(chip.text).toSet()
                is SalesOrderNumberSuggestion -> collectOrderDao.getInvoiceNumbersByOrderNumber(chip.text).toSet()
                is PhoneSuggestion -> collectOrderDao.getInvoiceNumbersByPhone(chip.text).toSet()
            }
        }
        // Intersect all sets
        return perChip.reduce { acc, next -> acc.intersect(next) }
    }

    private fun escapeForLike(input: String): String {
        return input.replace("!", "!!").replace("%", "!%").replace("_", "!_")
    }

    override suspend fun workOrderExists(workOrderId: WorkOrderId): Boolean {
        return workOrderDao.getCollectWorkOrderEntity(workOrderId) != null
    }

    override suspend fun getWorkOrderByIdSnapshot(workOrderId: WorkOrderId): WorkOrderWithOrderWithCustomers? {
        val relation = workOrderDao.getWorkOrder(workOrderId) ?: return null
        val wo = relation.collectWorkOrderEntity.toDomain()
        val items = relation.collectOrderWithCustomerRelation.toDomain()
        return WorkOrderWithOrderWithCustomers(
            collectWorkOrder = wo,
            collectOrderWithCustomerList = items
        )
    }
}
