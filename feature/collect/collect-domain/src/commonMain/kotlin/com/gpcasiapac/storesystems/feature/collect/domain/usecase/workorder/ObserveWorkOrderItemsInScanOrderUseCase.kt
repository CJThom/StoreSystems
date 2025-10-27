package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class ObserveWorkOrderItemsInScanOrderUseCase(
    private val orderRepository: OrderRepository
) {

    operator fun invoke(workOrderId: WorkOrderId): Flow<List<CollectOrderWithCustomer>> {
        return orderRepository.observeWorkOrderItemsInScanOrder(workOrderId = workOrderId)
    }

}