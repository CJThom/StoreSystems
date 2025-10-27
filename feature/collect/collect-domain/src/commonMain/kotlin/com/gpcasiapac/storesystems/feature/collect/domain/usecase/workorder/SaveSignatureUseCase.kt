package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.Signature
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlin.time.Clock

class SaveSignatureUseCase(
    private val orderRepository: OrderRepository
) {

    suspend operator fun invoke(
        workOrderId: WorkOrderId,
        signatureBase64: String,
        signedByName: String
    ) {

        val now = Clock.System.now()

        val signature = Signature(
            workOrderId = workOrderId,
            signatureBase64 = signatureBase64,
            signedAt = now,
            signedByName = signedByName
        )

        orderRepository.insertOrReplaceSignature(signature = signature)

    }

}