package com.gpcasiapac.storesystems.feature.collect.domain.usecase.order

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observe the total number of orders stored in the database, independent of filters or search.
 */
class ObserveOrderCountUseCase(
    private val orderLocalRepository: OrderLocalRepository,
) {

    operator fun invoke(): Flow<Int> {
        return orderLocalRepository.observeOrderCount()
    }

}