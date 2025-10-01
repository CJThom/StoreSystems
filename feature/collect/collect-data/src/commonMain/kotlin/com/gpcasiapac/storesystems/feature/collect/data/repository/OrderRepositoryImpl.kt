package com.gpcasiapac.storesystems.feature.collect.data.repository

import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.OrderDao
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toDomain
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toEntity
import com.gpcasiapac.storesystems.feature.collect.data.network.dto.OrderDto
import com.gpcasiapac.storesystems.feature.collect.data.network.source.OrderNetworkDataSource
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestionType
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OrderRepositoryImpl(
    private val orderDao: OrderDao,
    private val orderNetworkDataSource: OrderNetworkDataSource,
) : OrderRepository {

    // TODO: In Progress: Improve this with Dao query
    override fun getOrderListFlow(orderQuery: OrderQuery): Flow<List<Order>> {
        return orderDao.getAllAsFlow().map { orderEntityList ->
            val orderList: List<Order> = orderEntityList.toDomain()
            val query = orderQuery.searchText.trim().lowercase()
            if (query.isEmpty()) orderList else orderList.filter { o ->
                val name = if (o.customerType == CustomerType.B2B) {
                    o.customer.accountName.orEmpty()
                } else o.customer.fullName
                name.lowercase().contains(query) ||
                        o.invoiceNumber.lowercase().contains(query) ||
                        ((o.webOrderNumber ?: "").lowercase().contains(query))
            }
        }
    }

    override suspend fun refreshOrders(): Result<Unit> = runCatching {
        val orderDtoList: List<OrderDto> = orderNetworkDataSource.fetchOrders()
        val orderEntityList = orderDtoList.toEntity()

        orderDao.insertOrReplaceOrderEntity(orderEntityList)

    }

    // TODO: In Progress: Improve this with Dao query
    override suspend fun getOrderSearchSuggestionList(text: String): List<OrderSearchSuggestion> { // TODO: Should this return a flow?
        val q = text.trim()
        if (q.isEmpty()) return emptyList()

        fun escapeLike(input: String): String {
            if (input.isEmpty()) return input
            val escape = '!'
            val sb = StringBuilder(input.length * 2)
            for (c in input) {
                when (c) {
                    escape, '%', '_' -> sb.append(escape).append(c)
                    else -> sb.append(c)
                }
            }
            return sb.toString()
        }

        val prefix = escapeLike(q) + "%"
        val nameLimit = 5
        val numberLimit = 5
        val totalLimit = 8

        val names = orderDao.getNameSuggestionsPrefix(prefix, nameLimit)
        val invoices = orderDao.getInvoiceSuggestionsPrefix(prefix, numberLimit)
        val webs = orderDao.getWebOrderSuggestionsPrefix(prefix, numberLimit)

        val suggestions = buildList<OrderSearchSuggestion> {
            names.forEach { add(OrderSearchSuggestion(it, OrderSearchSuggestionType.NAME)) }
            invoices.forEach { add(OrderSearchSuggestion(it, OrderSearchSuggestionType.ORDER_NUMBER)) }
            webs.forEach { add(OrderSearchSuggestion(it, OrderSearchSuggestionType.ORDER_NUMBER)) }
        }
            .distinctBy { it.type to it.text }
            .take(totalLimit)
            .toMutableList()

        // Optional PHONE suggestion heuristic: only if looks like a real phone number and not duplicating an order number
        val digits = q.filter { it.isDigit() }
        val looksLikePhone = digits.length in 8..15 && (q.all { it.isDigit() || it in "+ -()" })
        val clashesWithOrderNumber = suggestions.any {
            it.type == OrderSearchSuggestionType.ORDER_NUMBER && it.text.equals(
                q,
                ignoreCase = true
            )
        }
        if (looksLikePhone && !clashesWithOrderNumber) {
            suggestions += OrderSearchSuggestion(q, OrderSearchSuggestionType.PHONE)
        }

        return suggestions
    }


}
