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
        val text = query.text
        val per = query.perKindLimit
        val include = query.includeKinds
        val selected = query.selected

        // Compute invoice scope from selected chips (intersection)
        val scope: Set<String> = resolveInvoiceScope(selected)

        val out = mutableListOf<SearchSuggestion>()
        if (text.isBlank()) {
            // Default suggestions: show customers; if chips are selected, constrain to scope
            if (SuggestionKind.CUSTOMER_NAME in include) {
                val seedLimit = if (scope.isEmpty()) query.maxTotal else query.maxTotal * 5
                val rows = collectOrderDao.getAllCustomerNames(limit = seedLimit)
                if (scope.isEmpty()) {
                    rows.forEach { row ->
                        val name = row.name.trim()
                        if (name.isNotEmpty()) out += CustomerNameSuggestion(
                            text = name,
                            customerType = CustomerType.B2C // TODO: get CustomerType from DAO
                        )
                    }
                } else {
                    // Filter names to only those within scope
                    for (row in rows) {
                        val name = row.name.trim()
                        if (name.isEmpty()) continue
                        val invoices = collectOrderDao.getInvoiceNumbersByCustomerName(name)
                        if (invoices.any { it in scope }) {
                            out += CustomerNameSuggestion(
                                text = name,
                                customerType = CustomerType.B2C // TODO: DAO provide type
                            )
                            if (out.size >= query.maxTotal) break
                        }
                    }
                }
            }
            return out
        }
        val contains = "%" + escapeForLike(text.trim()) + "%"
        if (SuggestionKind.CUSTOMER_NAME in include) {
            collectOrderDao.getCustomerNameSuggestionsPrefix(contains, per).forEach { row ->
                val name = row.name.trim()
                if (name.isNotEmpty()) out += CustomerNameSuggestion(
                    text = name,
                    customerType = CustomerType.B2C // TODO: get CustomerType from DAO
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
        // De-dup
        val deduped = out.distinctBy { it.kind to it.text.lowercase() }
        // Apply chip scope if present
        val filtered = if (scope.isEmpty()) deduped else deduped.filter { suggestion ->
            suggestionIntersectsScope(suggestion, scope)
        }
        val rank = mapOf(
            SuggestionKind.CUSTOMER_NAME to 0,
            SuggestionKind.INVOICE_NUMBER to 1,
            SuggestionKind.WEB_ORDER_NUMBER to 2,
            SuggestionKind.SALES_ORDER_NUMBER to 3,
            SuggestionKind.PHONE to 4,
        )
        val sorted = filtered.sortedWith(compareBy({ rank[it.kind] ?: 99 }, { it.text }))
        return if (sorted.size <= query.maxTotal) sorted else sorted.take(query.maxTotal)
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

    private suspend fun suggestionIntersectsScope(s: SearchSuggestion, scope: Set<String>): Boolean {
        return when (s) {
            is CustomerNameSuggestion -> collectOrderDao.getInvoiceNumbersByCustomerName(s.text).any { it in scope }
            is InvoiceNumberSuggestion -> s.text in scope
            is WebOrderNumberSuggestion -> collectOrderDao.getInvoiceNumbersByWebOrder(s.text).any { it in scope }
            is SalesOrderNumberSuggestion -> collectOrderDao.getInvoiceNumbersByOrderNumber(s.text).any { it in scope }
            is PhoneSuggestion -> collectOrderDao.getInvoiceNumbersByPhone(s.text).any { it in scope }
        }
    }

    override fun observeSearchSuggestions(query: SuggestionQuery): Flow<List<SearchSuggestion>> {
        // Recompute suggestions whenever orders or customer/customer fields change.
        // Observe the relation list to react to any DB updates that affect suggestions.
        return collectOrderDao.getCollectOrderWithCustomerRelationListFlow()
            .mapLatest { getSearchSuggestions(query) }
            .distinctUntilChanged()
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
