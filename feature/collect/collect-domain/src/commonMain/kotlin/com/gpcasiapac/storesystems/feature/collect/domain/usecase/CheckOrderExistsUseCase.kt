package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository

/**
 * Optimized existence check for an invoice number using a lightweight DAO query.
 * Returns a UseCaseResult so the caller can handle NotFound vs InvalidInput.
 */
class CheckOrderExistsUseCase(
    private val orderRepository: OrderRepository,
) {
    suspend operator fun invoke(invoiceNumber: String): UseCaseResult {
        val clean = invoiceNumber.trim()
        if (clean.isEmpty()) return UseCaseResult.Error.InvalidInput
        val exists = orderRepository.existsInvoice(clean)
        return if (exists) UseCaseResult.Exists(clean) else UseCaseResult.Error.NotFound(clean)
    }

    sealed interface UseCaseResult {
        data class Exists(val invoiceNumber: String) : UseCaseResult
        sealed class Error(val message: String) : UseCaseResult {
            data object InvalidInput : Error("Invoice number cannot be empty.")
            data class NotFound(val invoiceNumber: String) : Error("Order not found: \"$invoiceNumber\"")
        }
    }
}
