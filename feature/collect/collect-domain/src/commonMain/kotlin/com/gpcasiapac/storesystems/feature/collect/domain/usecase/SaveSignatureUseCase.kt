package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

class SaveSignatureUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(userRefId: String, base64Signature: String, signedByName: String? = null) =
        orderRepository.attachSignatureToLatestOpenWorkOrder(
            userRefId = userRefId,
            signature = base64Signature,
            signedByName = signedByName
        )
}