package com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection

import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

class ClearOrderSelectionUseCase(
    private val orderRepository: OrderRepository,
) {

    suspend operator fun invoke(workOrderId: WorkOrderId) {
        return orderRepository.clear(workOrderId = workOrderId)
    }

}
