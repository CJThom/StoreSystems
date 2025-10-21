package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.model.SearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import com.gpcasiapac.storesystems.feature.collect.domain.repository.SuggestionQuery

class GetOrderSearchSuggestionListUseCase(
    private val orderRepository: OrderRepository,
) {

    suspend operator fun invoke(text: String): List<SearchSuggestion> {
        return orderRepository.getSearchSuggestions(SuggestionQuery(text = text))
    }

}
