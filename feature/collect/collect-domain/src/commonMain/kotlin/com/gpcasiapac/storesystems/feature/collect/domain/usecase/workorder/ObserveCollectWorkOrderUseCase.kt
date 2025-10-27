package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectWorkOrder
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class ObserveCollectWorkOrderUseCase(
    private val orderRepository: OrderRepository
) {

    operator fun invoke(workOrderId: WorkOrderId): Flow<CollectWorkOrder?> {
        return orderRepository.getCollectWorkOrderFlow(workOrderId = workOrderId)
    }

}