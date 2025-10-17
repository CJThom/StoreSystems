package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class ObserveWorkOrderSignatureUseCase(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(userRefId: String): Flow<String?> =
        orderRepository.observeLatestOpenWorkOrderSignature(userRefId)
}
