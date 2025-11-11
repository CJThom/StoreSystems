package com.gpcasiapac.storesystems.feature.history.domain.usecase

import com.gpcasiapac.storesystems.feature.history.api.HistoryType
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem

/**
 * Generic entry point to fetch a single HistoryItem by type + id.
 * It delegates to type-specific loaders under the hood.
 */
class GetHistoryItemByIdUseCase(
    private val getCollectHistoryItemById: GetCollectHistoryItemByIdUseCase,
) {
    suspend operator fun invoke(type: HistoryType, id: String): Result<HistoryItem?> = runCatching {
        when (type) {
            HistoryType.ORDER_SUBMISSION -> getCollectHistoryItemById(id).getOrNull()
            else -> null
        }
    }
}
