package com.gpcasiapac.storesystems.feature.collect.data.network.source

import com.gpcasiapac.storesystems.common.networking.json.loadJsonResource
import com.gpcasiapac.storesystems.feature.collect.data.network.dto.OrderDto

/**
 * Common mock data source that loads orders from a JSON file in commonMain/resources.
 * Path: mock/orders.json
 */
class MockOrderNetworkDataSource : OrderNetworkDataSource {
    override suspend fun fetchOrders(): List<OrderDto> {
        return loadJsonResource("mock/orders.json")
    }
}
