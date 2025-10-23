package com.gpcasiapac.storesystems.feature.history.domain.usecase

import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case to observe all history items.
 */
class GetHistoryUseCase(
    private val historyRepository: HistoryRepository
) {
    operator fun invoke(): Flow<List<HistoryItem>> {
        return historyRepository.observeHistory()
    }
}
