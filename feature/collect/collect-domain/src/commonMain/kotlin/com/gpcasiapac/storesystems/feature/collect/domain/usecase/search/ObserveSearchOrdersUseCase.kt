package com.gpcasiapac.storesystems.feature.collect.domain.usecase.search

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.SearchQuery
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observe search results for the order list based on search text only.
 * This does not apply the main list filters or sort.
 */
class ObserveSearchOrdersUseCase(
    private val orderLocalRepository: OrderLocalRepository,
) {

    operator fun invoke(query: SearchQuery): Flow<List<CollectOrderWithCustomer>> {
        return orderLocalRepository.observeSearchOrders(query)
    }

}