package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

class SetWorkOrderCollectingTypeUseCase(
    private val orderRepository: OrderRepository
) {

    suspend operator fun invoke(workOrderId: WorkOrderId, type: CollectingType) {
        return orderRepository.setCollectingType(
            workOrderId = workOrderId,
            type = type
        )
    }

}
