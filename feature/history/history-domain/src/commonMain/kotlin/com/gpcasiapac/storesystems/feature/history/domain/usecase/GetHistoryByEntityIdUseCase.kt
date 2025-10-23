package com.gpcasiapac.storesystems.feature.history.domain.usecase

import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.repository.HistoryRepository

/**
 * Use case to get history for a specific entity.
 */
class GetHistoryByEntityIdUseCase(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(entityId: String): List<HistoryItem> {
        return historyRepository.getHistoryByEntityId(entityId)
    }
}
