package com.gpcasiapac.storesystems.feature.collect.data.network.source

import com.gpcasiapac.storesystems.feature.collect.data.network.dto.OrderDto
import com.gpcasiapac.storesystems.feature.collect.data.platform.ResourceReader
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * Common mock data source that loads orders from a JSON file in commonMain/resources.
 * Path: mock/orders.json
 */
class MockOrderNetworkDataSource : OrderNetworkDataSource {
    override suspend fun fetchOrders(): List<OrderDto> {
        val json = ResourceReader.readText("mock/orders.json")
        return OrderMockJsonParser.parse(json)
    }
}

/**
 * Common JSON parsing helper for mock order payloads.
 */
internal object OrderMockJsonParser {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun parse(text: String): List<OrderDto> {
        return json.decodeFromString(ListSerializer(OrderDto.serializer()), text)
    }
}
