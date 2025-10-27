package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

class SetWorkOrderCourierNameUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(workOrderId: WorkOrderId, name: String) {
         orderRepository.setCourierName(
            workOrderId = workOrderId,
            name = name
        )
    }

}