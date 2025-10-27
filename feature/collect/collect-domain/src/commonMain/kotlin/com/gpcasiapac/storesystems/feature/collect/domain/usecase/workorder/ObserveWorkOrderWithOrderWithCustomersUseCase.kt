package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.WorkOrderWithOrderWithCustomers
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class ObserveWorkOrderWithOrderWithCustomersUseCase(
    private val orderRepository: OrderRepository
) {

    operator fun invoke(workOrderId: WorkOrderId): Flow<WorkOrderWithOrderWithCustomers?> {
        return orderRepository.getWorkOrderWithOrderWithCustomerFlow(workOrderId = workOrderId)
    }

}