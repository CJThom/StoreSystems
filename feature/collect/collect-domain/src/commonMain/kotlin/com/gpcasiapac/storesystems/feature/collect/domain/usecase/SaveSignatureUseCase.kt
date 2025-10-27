package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

class SaveSignatureUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(
        workOrderId: WorkOrderId,
        base64Signature: String,
        signedByName: String? = null
    ) {

        orderRepository.attachSignature(
            workOrderId = workOrderId,
            signature = base64Signature,
            signedByName = signedByName
        )
    }

}