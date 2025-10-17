package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

class SetWorkOrderCollectingTypeUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(userRefId: String, type: CollectingType) =
        orderRepository.setCollectingType(userRefId, type)
}
