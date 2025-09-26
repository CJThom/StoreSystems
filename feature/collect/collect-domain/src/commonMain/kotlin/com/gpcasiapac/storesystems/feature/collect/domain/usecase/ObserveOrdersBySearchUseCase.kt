package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import com.gpcasiapac.storesystems.feature.collect.domain.repo.OrderQuery
import com.gpcasiapac.storesystems.feature.collect.domain.repo.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest

class ObserveOrdersBySearchUseCase(
    private val repository: OrderRepository,
) {
    /**
     * Accepts a flow of search text (e.g., from VM state) and emits filtered orders.
     * Debounces typing and switches underlying streams with flatMapLatest.
     */
    operator fun invoke(searchText: Flow<String>): Flow<List<Order>> {
        return searchText
            .debounce(150)
            .distinctUntilChanged()
            .flatMapLatest { text ->
                repository.observeOrders(OrderQuery(searchText = text))
            }
    }

}
