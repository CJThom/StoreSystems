package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestion
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

class GetOrderSearchSuggestionListUseCase(
    private val orderRepository: OrderRepository,
) {

    suspend operator fun invoke(text: String): List<OrderSearchSuggestion> {
        return orderRepository.getOrderSearchSuggestionList(text)
    }

}
