package com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

class ClearOrderSelectionUseCase(
    private val orderRepository: OrderRepository,
) {
    suspend operator fun invoke(userRefId: String) =
        orderRepository.clear(userRefId)
}
