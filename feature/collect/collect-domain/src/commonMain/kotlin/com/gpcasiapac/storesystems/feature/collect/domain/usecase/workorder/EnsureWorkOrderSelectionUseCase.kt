package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository

/**
 * Ensures there is a valid Work Order to use for selection.
 *
 * It never reads/writes user prefs; IDs are provided by the caller.
 */
class EnsureWorkOrderSelectionUseCase(
    private val orderLocalRepository: OrderLocalRepository,
    private val createWorkOrderUseCase: CreateWorkOrderUseCase,
) {

    suspend operator fun invoke(
        userId: UserId,
        selectedWorkOrderId: WorkOrderId?,
    ): UseCaseResult = try {
        if (selectedWorkOrderId != null && orderLocalRepository.workOrderExists(selectedWorkOrderId)) {
            UseCaseResult.AlreadySelected(selectedWorkOrderId)
        } else {
            when (val res = createWorkOrderUseCase(userId)) {
                is CreateWorkOrderUseCase.UseCaseResult.Success ->
                    UseCaseResult.CreatedNew(res.workOrderId)
                is CreateWorkOrderUseCase.UseCaseResult.Error ->
                    UseCaseResult.Error.Unexpected(res.message)
            }
        }
    } catch (t: Throwable) {
        UseCaseResult.Error.Unexpected(t.message ?: "Failed to ensure Work Order selection")
    }

    sealed interface UseCaseResult {
        data class AlreadySelected(val workOrderId: WorkOrderId) : UseCaseResult
        data class CreatedNew(val workOrderId: WorkOrderId) : UseCaseResult
        sealed class Error(open val message: String) : UseCaseResult {
            data class Unexpected(override val message: String) : Error(message)
        }
    }
}
