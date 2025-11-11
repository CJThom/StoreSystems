package com.gpcasiapac.storesystems.feature.collect.data.network.source

import com.gpcasiapac.storesystems.feature.collect.data.network.dto.CollectOrderDto
import com.gpcasiapac.storesystems.feature.collect.data.network.dto.SubmitCollectOrderRequestDto

/**
 * Contract for fetching Orders from the network.
 * For now, implemented by a mock data source that returns in-memory data.
 */
interface OrderNetworkDataSource {
    suspend fun fetchOrders(): List<CollectOrderDto>
    suspend fun submitOrder(request: SubmitCollectOrderRequestDto): Result<Unit>
}
