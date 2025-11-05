package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.first

/**
 * One-shot snapshot of ordered items for a Work Order using OrderRepository (no new repo).
 */
class GetWorkOrderItemsSnapshotUseCase(
    private val orderLocalRepository: OrderLocalRepository
) {
    suspend operator fun invoke(workOrderId: WorkOrderId): Result<List<CollectOrderWithCustomer>> = runCatching {
        val wo = orderLocalRepository.getWorkOrderByIdSnapshot(workOrderId)
            ?: error("Work order not found: $workOrderId")
        wo.collectOrderWithCustomerList
    }
}
