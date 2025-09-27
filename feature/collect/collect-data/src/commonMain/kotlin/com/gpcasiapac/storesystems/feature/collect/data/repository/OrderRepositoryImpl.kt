package com.gpcasiapac.storesystems.feature.collect.data.repository

import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.OrderDao
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toDomain
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toEntity
import com.gpcasiapac.storesystems.feature.collect.data.network.source.OrderNetworkDataSource
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import com.gpcasiapac.storesystems.feature.collect.domain.repo.OrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.repo.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OrderRepositoryImpl(
    private val orderDao: OrderDao,
    private val network: OrderNetworkDataSource,
) : OrderRepository {

    override fun getOrderListFlow(query: OrderQuery): Flow<List<Order>> {
        return orderDao
            .getAllAsFlow()
            .map { entities ->
                val domain = entities.toDomain()
                val q = query.searchText.trim().lowercase()
                if (q.isEmpty()) domain else domain.filter { o ->
                    o.customerName.lowercase().contains(q) ||
                        o.invoiceNumber.lowercase().contains(q) ||
                        ((o.webOrderNumber ?: "").lowercase().contains(q))
                }
            }
    }

    override suspend fun refreshOrders(): Result<Unit> = runCatching {
        // Fetch from network, map to entities, and persist into DB
        val dtos = network.fetchOrders()
        val entities = dtos.toEntity()
        // For simplicity, replace existing rows with the same primary key
        // by relying on DAO's onConflict strategy (should be REPLACE). If not,
        // consider adding a clearAll() or update strategy.
        withContext(Dispatchers.IO) {
            orderDao.insertAll(entities)
        }
    }

}
