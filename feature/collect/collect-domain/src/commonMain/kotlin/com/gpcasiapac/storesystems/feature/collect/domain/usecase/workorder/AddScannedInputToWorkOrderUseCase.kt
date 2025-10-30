package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.CheckOrderExistsUseCase

/**
 * Scan-specific adder that accepts raw input and returns a simple result
 * suitable for playing sounds/haptics in the UI.
 *
 * Internally delegates to EnsureAndApplyOrderSelectionDeltaUseCase with
 * toAdd = [invoice] and toRemove = emptyList().
 */
class AddScannedInputToWorkOrderUseCase(
    private val checkOrderExistsUseCase: CheckOrderExistsUseCase,
    private val ensureAndApplyOrderSelectionDeltaUseCase: EnsureAndApplyOrderSelectionDeltaUseCase,
) {

    suspend operator fun invoke(
        userId: UserId?,
        currentSelectedWorkOrderId: WorkOrderId?,
        rawInput: String,
    ): UseCaseResult {
        val trimmed = rawInput.trim()
        if (trimmed.isEmpty()) return UseCaseResult.InvalidInput

        // Validate the scanned invoice exists in the local DB
        when (val exists = checkOrderExistsUseCase(trimmed)) {
            is CheckOrderExistsUseCase.UseCaseResult.Error -> {
                // Not found or other validation error
                return UseCaseResult.NotFound(trimmed)
            }

            is CheckOrderExistsUseCase.UseCaseResult.Exists -> {
                val invoice = exists.invoiceNumber
                return when (
                    val apply = ensureAndApplyOrderSelectionDeltaUseCase(
                        userId = userId,
                        currentSelectedWorkOrderId = currentSelectedWorkOrderId,
                        toAdd = listOf(invoice),
                        toRemove = emptyList(),
                    )
                ) {
                    is EnsureAndApplyOrderSelectionDeltaUseCase.UseCaseResult.Error -> UseCaseResult.Error(
                        apply.message
                    )

                    is EnsureAndApplyOrderSelectionDeltaUseCase.UseCaseResult.Noop -> UseCaseResult.Error(
                        "Nothing to apply"
                    )

                    is EnsureAndApplyOrderSelectionDeltaUseCase.UseCaseResult.Summary -> {
                        when {
                            invoice in apply.added -> UseCaseResult.Added(invoice)
                            invoice in apply.duplicates -> UseCaseResult.Duplicate(invoice)
                            else -> UseCaseResult.Error("Unexpected result for invoice: $invoice")
                        }
                    }
                }
            }
        }
    }

    sealed interface UseCaseResult {
        data class Added(val invoiceNumber: String) : UseCaseResult
        data class Duplicate(val invoiceNumber: String) : UseCaseResult
        data class NotFound(val input: String) : UseCaseResult
        data object InvalidInput : UseCaseResult
        data class Error(val message: String) : UseCaseResult
    }

}
