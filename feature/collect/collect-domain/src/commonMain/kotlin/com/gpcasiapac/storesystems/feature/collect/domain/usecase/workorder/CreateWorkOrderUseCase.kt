package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.CancellationException

class CreateWorkOrderUseCase(
    private val orderRepository: OrderRepository,
) {

    suspend operator fun invoke(orderId: String, userRefId: String): UseCaseResult {
        val newWorkOrder = CollectWorkOrderEntity(
            workOrderId = workOrderId,
            userId = userRefId,
            createdAt = now,
            submittedAt = null,
            signature = null,
            signedAt = null,
            signedByName = null
        )
    }

    sealed interface UseCaseResult {
        data class Added(val invoiceNumber: String) : UseCaseResult
        data class Duplicate(val invoiceNumber: String) : UseCaseResult
        sealed class Error(val message: String) : UseCaseResult {
            data object InvalidInput : Error("Invoice number cannot be empty.")
            data class Unexpected(val reason: String) : Error(reason)
        }
    }
}
