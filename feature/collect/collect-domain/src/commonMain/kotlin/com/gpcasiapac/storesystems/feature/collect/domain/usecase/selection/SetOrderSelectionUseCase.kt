package com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderSelectionRepository

class SetOrderSelectionUseCase(
    private val orderSelectionRepository: OrderSelectionRepository,
) {
    suspend operator fun invoke(orderIds: List<String>, userRefId: String? = null) =
        orderSelectionRepository.setSelectedIdList(orderIds, userRefId)
}
