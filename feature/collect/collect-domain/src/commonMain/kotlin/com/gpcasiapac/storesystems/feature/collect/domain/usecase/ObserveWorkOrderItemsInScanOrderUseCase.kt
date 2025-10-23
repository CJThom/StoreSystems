package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class ObserveWorkOrderItemsInScanOrderUseCase(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(workOrderId: String): Flow<List<CollectOrderWithCustomer>> =
        orderRepository.observeWorkOrderItemsInScanOrder(workOrderId)
}
