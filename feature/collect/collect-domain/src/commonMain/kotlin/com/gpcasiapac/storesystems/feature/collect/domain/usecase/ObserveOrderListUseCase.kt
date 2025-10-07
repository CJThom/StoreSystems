package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class ObserveOrderListUseCase(
    private val orderRepository: OrderRepository,
) {

    operator fun invoke(orderQuery: OrderQuery): Flow<List<CollectOrderWithCustomerWithLineItems>> {
        return orderRepository.getOrderListFlow(orderQuery)
    }

}
