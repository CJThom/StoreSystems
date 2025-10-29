package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId

/**
 * Remove a single order from a Work Order.
 * Delegates to ApplyOrderSelectionDeltaUseCase to avoid duplicating transactional logic.
 * Returns a concise result that callers can use for feedback, but callers may ignore it.
 */
class RemoveOrderSelectionUseCase(
    private val applyDeltaUseCase: ApplyOrderSelectionDeltaUseCase,
) {

    suspend operator fun invoke(workOrderId: WorkOrderId, orderId: String): UseCaseResult {
        val id = orderId.trim()
        if (id.isEmpty()) return UseCaseResult.InvalidInput()
        return try {
            when (val r = applyDeltaUseCase(
                workOrderId = workOrderId,
                add = emptyList(),
                remove = listOf(id)
            )) {
                is ApplyOrderSelectionDeltaUseCase.Result.Noop -> UseCaseResult.Noop
                is ApplyOrderSelectionDeltaUseCase.Result.Summary -> {
                    if (r.removed > 0) UseCaseResult.Removed(invoiceNumber = id, removedCount = r.removed)
                    else UseCaseResult.Noop
                }
            }
        } catch (t: Throwable) {
            UseCaseResult.Error(t.message ?: "Failed to remove order from work order.")
        }
    }

    sealed interface UseCaseResult {
        /** The requested invoice was removed. removedCount should be 1 in normal cases. */
        data class Removed(val invoiceNumber: String, val removedCount: Int = 1) : UseCaseResult
        /** Nothing changed (e.g., invoice was not in the work order, or inputs invalid). */
        data object Noop : UseCaseResult
        /** Invalid input such as a blank invoice number. */
        data class InvalidInput(val message: String = "Invoice number cannot be empty.") : UseCaseResult
        /** Unexpected failure. */
        data class Error(val message: String) : UseCaseResult
    }

}