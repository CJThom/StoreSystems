package com.gpcasiapac.storesystems.feature.collect.data.network.source

import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.common.networking.json.loadJsonResource
import com.gpcasiapac.storesystems.feature.collect.data.network.dto.CollectOrderDto

/**
 * Common mock data source that loads orders from a JSON file in commonMain/resources.
 * Path: mock/orders.json
 */
class MockOrderNetworkDataSource(
    private val logger: Logger,
) : OrderNetworkDataSource {

    private val log = logger.withTag("MockOrderNetwork")

    override suspend fun fetchOrders(): List<CollectOrderDto> {
        log.d { "fetchOrders: start" }
        return try {
            val list: List<CollectOrderDto> = loadJsonResource("mock/orders.json")
            log.d { "fetchOrders: loaded ${list.size} orders" }
            list
        } catch (t: Throwable) {
            log.e(t) { "fetchOrders: failed" }
            throw t
        }
    }
}
