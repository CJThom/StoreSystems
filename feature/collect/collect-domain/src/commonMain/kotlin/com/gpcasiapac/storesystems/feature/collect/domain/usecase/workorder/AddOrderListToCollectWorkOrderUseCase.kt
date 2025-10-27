package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId

class AddOrderListToCollectWorkOrderUseCase(
    private val addOrderToCollectWorkOrderUseCase: AddOrderToCollectWorkOrderUseCase
) {

    // TODO: Optimise
    suspend operator fun invoke(workOrderId: WorkOrderId, orderIdList: List<String>) {
        orderIdList.forEach {
            addOrderToCollectWorkOrderUseCase(
                workOrderId = workOrderId,
                orderId = it
            )
        }
    }

}