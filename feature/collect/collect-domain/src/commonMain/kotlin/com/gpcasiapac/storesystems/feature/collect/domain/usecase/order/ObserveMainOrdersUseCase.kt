package com.gpcasiapac.storesystems.feature.collect.domain.usecase.order

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.MainOrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observe the main orders list driven by filters (e.g., CustomerType) and sort.
 * Thin wrapper around repository to keep VM decoupled and testable.
 */
class ObserveMainOrdersUseCase(
    private val orderLocalRepository: OrderLocalRepository,
) {
    operator fun invoke(query: MainOrderQuery): Flow<List<CollectOrderWithCustomer>> {
        return orderLocalRepository.observeMainOrders(query)
    }

}