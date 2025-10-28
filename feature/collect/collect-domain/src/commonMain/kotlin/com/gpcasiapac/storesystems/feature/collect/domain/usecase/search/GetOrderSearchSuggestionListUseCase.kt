package com.gpcasiapac.storesystems.feature.collect.domain.usecase.search

import com.gpcasiapac.storesystems.feature.collect.domain.model.SearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.model.SuggestionQuery
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

class GetOrderSearchSuggestionListUseCase(
    private val orderLocalRepository: OrderLocalRepository,
) {

    suspend operator fun invoke(text: String): List<SearchSuggestion> {
        return orderLocalRepository.getSearchSuggestions(SuggestionQuery(text = text))
    }

}