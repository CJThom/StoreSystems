package com.gpcasiapac.storesystems.feature.collect.domain.usecase.order

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

class FetchOrderListUseCase(
    private val orderRepository: OrderRepository,
) {

    suspend operator fun invoke(): Result<Unit> {
        return orderRepository.refreshOrders()
    }

}