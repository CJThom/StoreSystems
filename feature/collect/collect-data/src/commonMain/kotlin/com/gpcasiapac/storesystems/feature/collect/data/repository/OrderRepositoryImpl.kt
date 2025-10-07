package com.gpcasiapac.storesystems.feature.collect.data.repository

import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import com.gpcasiapac.storesystems.common.kotlin.util.StringUtils
import com.gpcasiapac.storesystems.feature.collect.data.local.db.AppDatabase
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.CollectOrderDao
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderCustomerEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderLineItemEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.CollectOrderWithCustomerWithLineItemsRelation
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toDomain
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toRelation
import com.gpcasiapac.storesystems.feature.collect.data.network.dto.CollectOrderDto
import com.gpcasiapac.storesystems.feature.collect.data.network.source.OrderNetworkDataSource
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class OrderRepositoryImpl(
    private val collectOrderDao: CollectOrderDao,
    private val database: AppDatabase,
    private val orderNetworkDataSource: OrderNetworkDataSource,
) : OrderRepository {

    // TODO: In Progress: Improve this with Dao query
    override fun getCollectOrderWithCustomerWithLineItemsListFlow(orderQuery: OrderQuery): Flow<List<CollectOrderWithCustomerWithLineItems>> {
        return collectOrderDao.getCollectOrderWithCustomerWithLineItemsRelationListFlow()
            .map { orderEntityList ->
            val collectOrderList: List<CollectOrderWithCustomerWithLineItems> = orderEntityList.toDomain()
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
        return collectOrderDao.getCollectOrderWithCustomerWithLineItemsRelationFlow(invoiceNumber = invoiceNumber).map { it.toDomain() }
    }

    override fun getCollectOrderWithCustomerListFlow(): Flow<List<CollectOrderWithCustomer>> {
        return collectOrderDao.getCollectOrderWithCustomerRelationListFlow().map { it.toDomain() }
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

    // TODO: In Progress: Improve this with Dao query
    override suspend fun getOrderSearchSuggestionList(text: String): List<OrderSearchSuggestion> { // TODO: Should this return a flow?
        return emptyList()
//        val q = text.trim()
//        if (q.isEmpty()) return emptyList()
//
//        fun escapeLike(input: String): String {
//            if (input.isEmpty()) return input
//            val escape = '!'
//            val sb = StringBuilder(input.length * 2)
//            for (c in input) {
//                when (c) {
//                    escape, '%', '_' -> sb.append(escape).append(c)
//                    else -> sb.append(c)
//                }
//            }
//            return sb.toString()
//        }
//
//        val prefix = escapeLike(q) + "%"
//        val nameLimit = 5
//        val numberLimit = 5
//        val totalLimit = 8
//
//        val names = collectOrderDao.getNameSuggestionsPrefix(prefix, nameLimit)
//        val invoices = collectOrderDao.getInvoiceSuggestionsPrefix(prefix, numberLimit)
//        val webs = collectOrderDao.getWebOrderSuggestionsPrefix(prefix, numberLimit)
//
//        val suggestions = buildList<OrderSearchSuggestion> {
//            names.forEach { add(OrderSearchSuggestion(it, OrderSearchSuggestionType.NAME)) }
//            invoices.forEach { add(OrderSearchSuggestion(it, OrderSearchSuggestionType.ORDER_NUMBER)) }
//            webs.forEach { add(OrderSearchSuggestion(it, OrderSearchSuggestionType.ORDER_NUMBER)) }
//        }
//            .distinctBy { it.type to it.text }
//            .take(totalLimit)
//            .toMutableList()
//
//        // Optional PHONE suggestion heuristic: only if looks like a real phone number and not duplicating an order number
//        val digits = q.filter { it.isDigit() }
//        val looksLikePhone = digits.length in 8..15 && (q.all { it.isDigit() || it in "+ -()" })
//        val clashesWithOrderNumber = suggestions.any {
//            it.type == OrderSearchSuggestionType.ORDER_NUMBER && it.text.equals(
//                q,
//                ignoreCase = true
//            )
//        }
//        if (looksLikePhone && !clashesWithOrderNumber) {
//            suggestions += OrderSearchSuggestion(q, OrderSearchSuggestionType.PHONE)
//        }
//
//        return suggestions
    }


}
