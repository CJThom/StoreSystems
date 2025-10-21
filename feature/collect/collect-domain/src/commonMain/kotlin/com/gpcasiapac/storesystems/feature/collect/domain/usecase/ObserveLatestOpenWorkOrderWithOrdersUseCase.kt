package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.model.WorkOrderWithOrderWithCustomers
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class ObserveLatestOpenWorkOrderWithOrdersUseCase(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(userRefId: String): Flow<WorkOrderWithOrderWithCustomers?> =
        orderRepository.observeLatestOpenWorkOrderWithOrders(userRefId)
}
