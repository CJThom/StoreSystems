package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import kotlinx.coroutines.flow.Flow

class ObserveCollectOrderWithCustomerWithLineItemsListUseCase(
    private val orderLocalRepository: OrderLocalRepository,
) {

    operator fun invoke(workOrderId: WorkOrderId): Flow<List<CollectOrderWithCustomerWithLineItems>> {
        return orderLocalRepository.getCollectOrderWithCustomerWithLineItemsListFlow(workOrderId = workOrderId)
    }

}