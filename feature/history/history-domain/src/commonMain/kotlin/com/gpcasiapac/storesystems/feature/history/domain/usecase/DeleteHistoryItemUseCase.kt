package com.gpcasiapac.storesystems.feature.history.domain.usecase

import com.gpcasiapac.storesystems.feature.history.domain.repository.HistoryRepository

/**
 * Use case to delete a history item.
 */
class DeleteHistoryItemUseCase(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return historyRepository.deleteHistoryItem(id)
    }
}
