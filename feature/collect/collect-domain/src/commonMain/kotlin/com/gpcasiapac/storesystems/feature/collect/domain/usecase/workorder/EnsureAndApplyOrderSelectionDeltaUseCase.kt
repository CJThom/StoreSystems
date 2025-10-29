package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
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
    sealed interface Result {
        data object Noop : Result
        data class Summary(
            val workOrderId: WorkOrderId,
            val added: List<String>,
            val duplicates: List<String>,
            val removed: Int,
        ) : Result
        data class Error(val message: String) : Result
    }

    suspend operator fun invoke(
        userId: UserId?,
        currentSelectedWorkOrderId: WorkOrderId?,
        toAdd: Collection<String>,
        toRemove: Collection<String>,
    ): Result {
        if (userId == null) return Result.Error("No user logged in")
        val addNorm = toAdd.map { it.trim() }.filter { it.isNotEmpty() }
        val removeNorm = toRemove.map { it.trim() }.filter { it.isNotEmpty() }
        if (addNorm.isEmpty() && removeNorm.isEmpty()) return Result.Noop

        val workOrderId: WorkOrderId? = if (addNorm.isNotEmpty()) {
            when (val ensured = ensureWorkOrderSelectionUseCase(userId, currentSelectedWorkOrderId)) {
                is EnsureWorkOrderSelectionUseCase.UseCaseResult.AlreadySelected -> ensured.workOrderId
                is EnsureWorkOrderSelectionUseCase.UseCaseResult.CreatedNew -> {
                    when (val upd = updateSelectedWorkOrderIdUseCase(userId, ensured.workOrderId)) {
                        is UpdateSelectedWorkOrderIdUseCase.UseCaseResult.Success -> ensured.workOrderId
                        is UpdateSelectedWorkOrderIdUseCase.UseCaseResult.Error -> return Result.Error(upd.message)
                    }
                }
                is EnsureWorkOrderSelectionUseCase.UseCaseResult.Error -> return Result.Error(ensured.message)
            }
        } else {
            currentSelectedWorkOrderId
        }

        if (workOrderId == null) return Result.Noop

        return when (val r = applyDeltaUseCase(workOrderId, addNorm, removeNorm)) {
            is ApplyOrderSelectionDeltaUseCase.Result.Noop -> Result.Noop
            is ApplyOrderSelectionDeltaUseCase.Result.Summary -> Result.Summary(
                workOrderId = workOrderId,
                added = r.added,
                duplicates = r.duplicates,
                removed = r.removed,
            )
        }
    }
}