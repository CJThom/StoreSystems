package com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

class AddOrderSelectionUseCase(
    private val orderRepository: OrderRepository,
) {
    suspend operator fun invoke(orderId: String, userRefId: String) =
        orderRepository.addSelectedId(orderId, userRefId)
}
