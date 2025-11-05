package com.gpcasiapac.storesystems.feature.history.domain.usecase

import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlin.time.ExperimentalTime

/**
 * Combines per-type history streams into a single ordered list.
 */
class ObserveUnifiedHistoryUseCase(
    private val observeCollectHistoryUseCase: ObserveCollectHistoryUseCase,
) {
    @OptIn(ExperimentalTime::class)
    operator fun invoke(): Flow<List<HistoryItem>> {
        val sources: List<Flow<List<HistoryItem>>> = listOf(
            observeCollectHistoryUseCase()
        )
        if (sources.isEmpty()) return flowOf(emptyList())
        return combine(sources) { lists ->
            lists.flatMap { it }
        }
    }
}