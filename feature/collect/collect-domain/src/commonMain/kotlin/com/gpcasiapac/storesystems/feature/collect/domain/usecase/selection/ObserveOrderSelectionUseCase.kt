package com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class ObserveOrderSelectionUseCase(
    private val orderRepository: OrderRepository,
) {
    operator fun invoke(userRefId: String): Flow<Set<String>> =
        orderRepository.getSelectedIdListFlow(userRefId)
}
