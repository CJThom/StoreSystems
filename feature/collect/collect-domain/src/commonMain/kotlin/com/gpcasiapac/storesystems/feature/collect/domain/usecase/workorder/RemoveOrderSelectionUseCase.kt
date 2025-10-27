package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

class RemoveOrderSelectionUseCase(
    private val orderRepository: OrderRepository,
) {

    suspend operator fun invoke(workOrderId: WorkOrderId, orderId: String) {
        return orderRepository.removeWorkOrderItem(
            workOrderId = workOrderId,
            orderId = orderId
        )
    }

}