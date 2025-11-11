package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.UpdateSelectedWorkOrderIdUseCase

/**
 * Orchestrator that ensures/persists the selected Work Order when adding, then applies
 * a delta (remove first, then add) atomically using ApplyOrderSelectionDeltaUseCase.
 */
class EnsureAndApplyOrderSelectionDeltaUseCase(
    private val ensureWorkOrderSelectionUseCase: EnsureWorkOrderSelectionUseCase,
    private val updateSelectedWorkOrderIdUseCase: UpdateSelectedWorkOrderIdUseCase,
    private val applyDeltaUseCase: ApplyOrderSelectionDeltaUseCase,
) {

    suspend operator fun invoke(
        userId: UserId?,
        currentSelectedWorkOrderId: WorkOrderId?,
        toAdd: Collection<InvoiceNumber>,
        toRemove: Collection<InvoiceNumber>,
    ): UseCaseResult {
        if (userId == null) return UseCaseResult.Error("No user logged in")

        if (toAdd.isEmpty() && toRemove.isEmpty()) return UseCaseResult.Noop

        val workOrderId: WorkOrderId? = if (toAdd.isNotEmpty()) {
            when (
                val ensured = ensureWorkOrderSelectionUseCase(
                    userId = userId,
                    selectedWorkOrderId = currentSelectedWorkOrderId
                )
            ) {
                is EnsureWorkOrderSelectionUseCase.UseCaseResult.AlreadySelected -> ensured.workOrderId
                is EnsureWorkOrderSelectionUseCase.UseCaseResult.CreatedNew -> {
                    when (
                        val updated = updateSelectedWorkOrderIdUseCase(
                            userId = userId,
                            selectedWorkOrderId = ensured.workOrderId
                        )
                    ) {
                        is UpdateSelectedWorkOrderIdUseCase.UseCaseResult.Success -> ensured.workOrderId
                        is UpdateSelectedWorkOrderIdUseCase.UseCaseResult.Error -> return UseCaseResult.Error(
                            updated.message
                        )
                    }
                }

                is EnsureWorkOrderSelectionUseCase.UseCaseResult.Error -> return UseCaseResult.Error(
                    ensured.message
                )
            }
        } else {
            currentSelectedWorkOrderId
        }

        if (workOrderId == null) return UseCaseResult.Noop

        return when (
            val result = applyDeltaUseCase(
                workOrderId = workOrderId,
                add = toAdd,
                remove = toRemove
            )
        ) {
            is ApplyOrderSelectionDeltaUseCase.Result.Noop -> UseCaseResult.Noop
            is ApplyOrderSelectionDeltaUseCase.Result.Summary -> UseCaseResult.Summary(
                workOrderId = workOrderId,
                added = result.added,
                duplicates = result.duplicates,
                removed = result.removed,
            )
        }
    }

    sealed interface UseCaseResult {
        data object Noop : UseCaseResult
        data class Summary(
            val workOrderId: WorkOrderId,
            val added: List<InvoiceNumber>,
            val duplicates: List<InvoiceNumber>,
            val removed: Int,
        ) : UseCaseResult

        data class Error(val message: String) : UseCaseResult
    }

}