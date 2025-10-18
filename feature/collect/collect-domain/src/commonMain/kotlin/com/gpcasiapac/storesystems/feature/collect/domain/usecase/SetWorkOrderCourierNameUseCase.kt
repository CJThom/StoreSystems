package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

class SetWorkOrderCourierNameUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(userRefId: String, name: String) =
        orderRepository.setCourierName(userRefId, name)
}
