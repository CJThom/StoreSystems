package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

class DeleteWorkOrderUseCase(
    private val orderRepository: OrderRepository,
) {

    suspend operator fun invoke(workOrderId: WorkOrderId) {
        return orderRepository.deleteWorkOrder(workOrderId = workOrderId)
    }

}