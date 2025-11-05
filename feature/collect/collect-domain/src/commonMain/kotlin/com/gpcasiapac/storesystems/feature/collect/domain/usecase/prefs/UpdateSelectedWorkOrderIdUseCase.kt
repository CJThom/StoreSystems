package com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs

import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.CollectUserPrefsRepository

/**
 * Preferred minimal-dependency version:
 * Updates only the selected Work Order ID in user prefs, scoped by user.
 */
class UpdateSelectedWorkOrderIdUseCase(
    private val collectUserPrefsRepository: CollectUserPrefsRepository,
    private val createCollectUserPrefsUseCase: CreateCollectUserPrefsUseCase
) {

    suspend operator fun invoke(
        userId: UserId,
        selectedWorkOrderId: WorkOrderId?,
    ): UseCaseResult = try {
        val updatedRow = collectUserPrefsRepository.setSelectedWorkOrderId(
            userId = userId,
            selectedWorkOrderId = selectedWorkOrderId
        )
        if (updatedRow == 0) {
            // No existing row; create with sensible defaults
            createCollectUserPrefsUseCase(
                userId = userId,
                selectedWorkOrderId = selectedWorkOrderId
            )
        }
        UseCaseResult.Success(selectedWorkOrderId)
    } catch (t: Throwable) {
        UseCaseResult.Error.Unexpected(t.message ?: "Failed to update selected Work Order ID")
    }

    sealed interface UseCaseResult {
        data class Success(val selectedWorkOrderId: WorkOrderId?) : UseCaseResult
        sealed class Error(open val message: String) : UseCaseResult {
            data class Unexpected(override val message: String) : Error(message)
        }
    }
}
