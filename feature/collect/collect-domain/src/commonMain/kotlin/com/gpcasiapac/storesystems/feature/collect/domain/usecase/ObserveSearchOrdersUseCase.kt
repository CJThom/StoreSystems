package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import com.gpcasiapac.storesystems.feature.collect.domain.repository.SearchQuery
import kotlinx.coroutines.flow.Flow

/**
 * Observe search results for the order list based on search text only.
 * This does not apply the main list filters or sort.
 */
class ObserveSearchOrdersUseCase(
    private val orderRepository: OrderRepository,
) {

    operator fun invoke(text: String): Flow<List<CollectOrderWithCustomer>> {
        return orderRepository.observeSearchOrders(SearchQuery(text))
    }

}
