package com.gpcasiapac.storesystems.feature.collect.data.repository
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toDomain
import com.gpcasiapac.storesystems.feature.collect.data.network.source.OrderNetworkDataSource
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRemoteRepository

/** Network-only implementation that fetches orders and maps them to domain models. */
class OrderRemoteRepositoryImpl(
    private val network: OrderNetworkDataSource,
) : OrderRemoteRepository {
    override suspend fun fetchOrders(): List<CollectOrderWithCustomerWithLineItems> {
        val dtos = network.fetchOrders()
        // Map CollectOrderDto directly to domain models (no relation hop)
        return dtos.toDomain()
    }
}
