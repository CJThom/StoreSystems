package com.gpcasiapac.storesystems.feature.history.domain.usecase

import com.gpcasiapac.storesystems.feature.history.api.HistoryType
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Generic entry point to fetch a single HistoryItem by type + id.
 * It delegates to type-specific loaders under the hood.
 */
class ObserveHistoryItemByIdUseCase(
    private val observeCollectHistoryItemById: ObserveCollectHistoryItemByIdUseCase,
) {
    suspend operator fun invoke(type: HistoryType, id: String): Flow<HistoryItem> {
        return when (type) {
            HistoryType.ORDER_SUBMISSION -> observeCollectHistoryItemById(id)
            else -> emptyFlow()
        }
    }
}
