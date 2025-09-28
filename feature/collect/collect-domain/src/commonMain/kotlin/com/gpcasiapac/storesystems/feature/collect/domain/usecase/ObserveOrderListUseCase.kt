package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class ObserveOrderListUseCase(
    private val orderRepository: OrderRepository,
) {

    operator fun invoke(query: OrderQuery): Flow<List<Order>> {
        return orderRepository.getOrderListFlow(query)
    }

}
