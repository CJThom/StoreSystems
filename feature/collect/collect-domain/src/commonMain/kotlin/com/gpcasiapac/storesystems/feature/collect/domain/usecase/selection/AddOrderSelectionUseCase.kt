package com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderSelectionRepository

class AddOrderSelectionUseCase(
    private val orderSelectionRepository: OrderSelectionRepository,
) {
    suspend operator fun invoke(orderId: String, userRefId: String? = null) =
        orderSelectionRepository.addSelectedId(orderId, userRefId)
}
