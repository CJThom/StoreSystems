package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class GetCollectOrderWithCustomerListFlowUseCase(
    private val orderRepository: OrderRepository,
) {

    operator fun invoke(): Flow<List<CollectOrderWithCustomer>> {
        return orderRepository.getCollectOrderWithCustomerListFlow()
    }

}
