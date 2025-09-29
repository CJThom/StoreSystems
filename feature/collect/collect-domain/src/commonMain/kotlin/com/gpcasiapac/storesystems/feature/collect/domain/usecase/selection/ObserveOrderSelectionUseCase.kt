package com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderSelectionRepository
import kotlinx.coroutines.flow.Flow

class ObserveOrderSelectionUseCase(
    private val orderSelectionRepository: OrderSelectionRepository,
) {
    operator fun invoke(userRefId: String? = null): Flow<Set<String>> =
        orderSelectionRepository.getSelectedIdListFlow(userRefId)
}
