package com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderSelectionRepository

class ClearOrderSelectionUseCase(
    private val orderSelectionRepository: OrderSelectionRepository,
) {
    suspend operator fun invoke(userRefId: String? = null) =
        orderSelectionRepository.clear(userRefId)
}
