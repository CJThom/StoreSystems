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

    sealed interface Result {
        data class Added(val invoiceNumber: String) : Result
        data class Duplicate(val invoiceNumber: String) : Result
        data class NotFound(val input: String) : Result
        data object InvalidInput : Result
        data class Error(val message: String) : Result
    }

    suspend operator fun invoke(
        userId: UserId?,
        currentSelectedWorkOrderId: WorkOrderId?,
        rawInput: String,
    ): Result {
        val trimmed = rawInput.trim()
        if (trimmed.isEmpty()) return Result.InvalidInput

        // Validate the scanned invoice exists in the local DB
        when (val exists = checkOrderExistsUseCase(trimmed)) {
            is CheckOrderExistsUseCase.UseCaseResult.Error -> {
                // Not found or other validation error
                return Result.NotFound(trimmed)
            }
            is CheckOrderExistsUseCase.UseCaseResult.Exists -> {
                val invoice = exists.invoiceNumber
                return when (val apply = ensureAndApplyOrderSelectionDeltaUseCase(
                    userId = userId,
                    currentSelectedWorkOrderId = currentSelectedWorkOrderId,
                    toAdd = listOf(invoice),
                    toRemove = emptyList(),
                )) {
                    is EnsureAndApplyOrderSelectionDeltaUseCase.Result.Error -> Result.Error(apply.message)
                    is EnsureAndApplyOrderSelectionDeltaUseCase.Result.Noop -> Result.Error("Nothing to apply")
                    is EnsureAndApplyOrderSelectionDeltaUseCase.Result.Summary -> {
                        when {
                            invoice in apply.added -> Result.Added(invoice)
                            invoice in apply.duplicates -> Result.Duplicate(invoice)
                            else -> Result.Error("Unexpected result for invoice: $invoice")
                        }
                    }
                }
            }
        }
    }
}
