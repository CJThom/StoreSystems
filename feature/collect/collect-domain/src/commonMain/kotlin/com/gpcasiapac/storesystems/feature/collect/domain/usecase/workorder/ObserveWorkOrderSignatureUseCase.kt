package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.Signature
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class ObserveWorkOrderSignatureUseCase(
    private val orderRepository: OrderRepository
) {

    operator fun invoke(workOrderId: WorkOrderId): Flow<Signature?> {
        return orderRepository.getWorkOrderSignatureFlow(workOrderId = workOrderId)
    }

}