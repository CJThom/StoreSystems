package com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

class SetOrderSelectionUseCase(
    private val orderRepository: OrderRepository,
) {
    suspend operator fun invoke(orderIds: List<String>, userRefId: String) =
        orderRepository.setSelectedIdList(orderIds, userRefId)
}
