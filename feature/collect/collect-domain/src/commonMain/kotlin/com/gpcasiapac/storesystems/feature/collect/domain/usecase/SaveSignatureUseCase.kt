package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

class SaveSignatureUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(base64Signature: String, invoiceNumber: List<String>) =
        orderRepository.saveSignature(signature = base64Signature, invoiceNumber = invoiceNumber)
}