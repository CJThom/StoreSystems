package com.gpcasiapac.storesystems.feature.collect.domain.usecase.order

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

/**
 * Optimized existence check for an invoice number using a lightweight DAO query.
 * Returns a UseCaseResult so the caller can handle NotFound vs InvalidInput.
 */
class CheckOrderExistsUseCase(
    private val orderRepository: OrderRepository,
) {

    suspend operator fun invoke(invoiceNumber: String): UseCaseResult {
        val exists = orderRepository.existsInvoice(invoiceNumber)
        return if (exists) {
            UseCaseResult.Exists(invoiceNumber)
        } else {
            UseCaseResult.Error.NotFound(invoiceNumber)
        }
    }

    sealed interface UseCaseResult {
        data class Exists(val invoiceNumber: String) : UseCaseResult
        sealed class Error(val message: String) : UseCaseResult {
            data class NotFound(val invoiceNumber: String) :
                Error("Order not found: \"$invoiceNumber\"")
        }
    }

}