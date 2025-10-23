package com.gpcasiapac.storesystems.feature.history.domain.usecase

import com.gpcasiapac.storesystems.feature.history.domain.repository.HistoryRepository

/**
 * Use case to retry a failed history item.
 */
class RetryHistoryItemUseCase(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return historyRepository.retryHistoryItem(id)
    }
}
