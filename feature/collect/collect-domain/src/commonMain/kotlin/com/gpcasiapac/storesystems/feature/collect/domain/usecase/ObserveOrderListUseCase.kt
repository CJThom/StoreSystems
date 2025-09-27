package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import com.gpcasiapac.storesystems.feature.collect.domain.repo.OrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.repo.OrderRepository
import kotlinx.coroutines.flow.Flow

class ObserveOrderListUseCase(
    private val repository: OrderRepository,
) {

    operator fun invoke(query: OrderQuery): Flow<List<Order>> {
        return repository.getOrderListFlow(query)
    }

}
