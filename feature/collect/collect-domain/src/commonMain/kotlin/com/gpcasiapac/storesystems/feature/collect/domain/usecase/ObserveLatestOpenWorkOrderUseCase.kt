package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectWorkOrder
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class ObserveLatestOpenWorkOrderUseCase(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(userRefId: String): Flow<CollectWorkOrder?> =
        orderRepository.observeLatestOpenWorkOrder(userRefId)
}