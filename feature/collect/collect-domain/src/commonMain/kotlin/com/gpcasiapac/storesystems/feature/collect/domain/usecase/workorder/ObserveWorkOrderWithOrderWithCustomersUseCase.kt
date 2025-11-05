package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.WorkOrderWithOrderWithCustomers
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import kotlinx.coroutines.flow.Flow

class ObserveWorkOrderWithOrderWithCustomersUseCase(
    private val orderLocalRepository: OrderLocalRepository,
) {

    operator fun invoke(workOrderId: WorkOrderId): Flow<WorkOrderWithOrderWithCustomers?> {
        return orderLocalRepository.getWorkOrderWithOrderWithCustomerFlow(workOrderId = workOrderId)
    }

}