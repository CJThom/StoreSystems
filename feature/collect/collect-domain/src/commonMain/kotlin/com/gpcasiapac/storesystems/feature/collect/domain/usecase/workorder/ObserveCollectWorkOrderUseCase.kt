package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectWorkOrder
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class ObserveCollectWorkOrderUseCase(
    private val orderLocalRepository: OrderLocalRepository,
) {

    operator fun invoke(workOrderId: WorkOrderId): Flow<CollectWorkOrder?> {
        return orderLocalRepository.getCollectWorkOrderFlow(workOrderId = workOrderId)
    }

}