package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.first

/**
 * One-shot snapshot of ordered items for a Work Order using OrderRepository (no new repo).
 */
class GetWorkOrderItemsSnapshotUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(workOrderId: String): Result<List<CollectOrderWithCustomer>> = runCatching {
        val wo = orderRepository.getWorkOrderByIdSnapshot(workOrderId)
            ?: error("Work order not found: $workOrderId")
        wo.collectOrderWithCustomerList
    }
}
