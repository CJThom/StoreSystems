package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrder
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Returns the default Order to display on the detail screen.
 * Current behavior: the first order from the repository list (no search filter).
 */
class GetDefaultOrderUseCase(
    private val orderRepository: OrderRepository,
) {
    operator fun invoke(): Flow<CollectOrder?> {
        return orderRepository.getOrderListFlow(OrderQuery(searchText = ""))
            .map { it.firstOrNull() }
    }
}
