package com.gpcasiapac.storesystems.feature.collect.domain.usecase.order

import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository

/**
 * Optimized existence check for an invoice number using a lightweight DAO query.
 * Returns a UseCaseResult so the caller can handle NotFound vs InvalidInput.
 */
class ValidateScannedInvoiceInputUseCase(
    private val orderLocalRepository: OrderLocalRepository,
) {

    suspend operator fun invoke(rawInput: String): UseCaseResult {
        // TODO: Get real invoice
        val exists = orderLocalRepository.existsInvoice(rawInput)
        return if (exists) {
            UseCaseResult.Exists(invoiceNumber = InvoiceNumber(rawInput))
        } else {
            UseCaseResult.Error.NotFound(rawInput)
        }
    }

    sealed interface UseCaseResult {
        data class Exists(val invoiceNumber: InvoiceNumber) : UseCaseResult
        sealed class Error(val message: String) : UseCaseResult {
            data class NotFound(val invoiceNumber: String) : Error(
                "Order not found: \"$invoiceNumber\""
            )
        }
    }

}