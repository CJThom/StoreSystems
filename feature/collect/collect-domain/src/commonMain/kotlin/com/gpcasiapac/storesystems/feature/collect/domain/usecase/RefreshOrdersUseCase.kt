package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.repo.OrderRepository

class RefreshOrdersUseCase(
    private val repository: OrderRepository,
) {

    suspend operator fun invoke(): Result<Unit> {
        return repository.refreshOrders()
    }

}
