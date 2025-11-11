package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository

class SetWorkOrderIdVerifiedUseCase(
    private val orderLocalRepository: OrderLocalRepository,
) {

    suspend operator fun invoke(workOrderId: WorkOrderId, checked: Boolean) {
        orderLocalRepository.setIdVerified(workOrderId, checked)
    }
}
