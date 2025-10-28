package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

class DeleteWorkOrderUseCase(
    private val orderLocalRepository: OrderLocalRepository,
) {

    suspend operator fun invoke(workOrderId: WorkOrderId) {
        return orderLocalRepository.deleteWorkOrder(workOrderId = workOrderId)
    }

}