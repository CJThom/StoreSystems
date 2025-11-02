package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.UpdateSelectedWorkOrderIdUseCase

/**
 * Application-layer orchestrator for the single-add flow.
 *
 * IDs are passed in by the caller. It will:
 * 1) Ensure there is a valid Work Order id (create only when adding).
 * 2) If a new WO was created, persist the selection in prefs.
 * 3) Add the given order to the work order.
 */
@Deprecated("Use EnsureAndApplyOrderSelectionDeltaUseCase()")
class EnsureAndAddOrderToWorkOrderUseCase(
    private val ensureWorkOrderSelectionUseCase: EnsureWorkOrderSelectionUseCase,
    private val updateSelectedWorkOrderIdUseCase: UpdateSelectedWorkOrderIdUseCase,
    private val addOrderToCollectWorkOrderUseCase: AddOrderToCollectWorkOrderUseCase,
) {

    suspend operator fun invoke(
        userId: UserId?,
        currentSelectedWorkOrderId: WorkOrderId?,
        invoiceNumber: InvoiceNumber,
    ): UseCaseResult {

        if (userId == null) return UseCaseResult.Error.NoUser

        // Step 1: Ensure or create a Work Order id (creation only happens because caller is adding)
        val workOrderId: WorkOrderId = when (
            val ensured = ensureWorkOrderSelectionUseCase(
                userId = userId,
                selectedWorkOrderId = currentSelectedWorkOrderId
            )
        ) {
            is EnsureWorkOrderSelectionUseCase.UseCaseResult.AlreadySelected -> ensured.workOrderId
            is EnsureWorkOrderSelectionUseCase.UseCaseResult.CreatedNew -> {
                // Step 2: Persist the new selection
                when (
                    val upd = updateSelectedWorkOrderIdUseCase(
                        userId = userId,
                        selectedWorkOrderId = ensured.workOrderId
                    )
                ) {
                    is UpdateSelectedWorkOrderIdUseCase.UseCaseResult.Success -> ensured.workOrderId
                    is UpdateSelectedWorkOrderIdUseCase.UseCaseResult.Error -> {
                        return UseCaseResult.Error.PersistSelectionFailed(upd.message)
                    }

                }
            }

            is EnsureWorkOrderSelectionUseCase.UseCaseResult.Error -> {
                return UseCaseResult.Error.EnsureFailed(ensured.message)
            }
        }

        // Step 3: Add the order
        return when (
            val add = addOrderToCollectWorkOrderUseCase(
                workOrderId = workOrderId,
                invoiceNumber = invoiceNumber
            )
        ) {
            is AddOrderToCollectWorkOrderUseCase.UseCaseResult.Added -> {
                UseCaseResult.Success(
                    workOrderId = workOrderId,
                    outcome = UseCaseResult.Success.AddOutcome.Added(invoiceNumber)
                )
            }

            is AddOrderToCollectWorkOrderUseCase.UseCaseResult.Duplicate -> {
                UseCaseResult.Success(
                    workOrderId = workOrderId,
                    outcome = UseCaseResult.Success.AddOutcome.Duplicate(invoiceNumber)
                )
            }

            is AddOrderToCollectWorkOrderUseCase.UseCaseResult.Error -> {
                UseCaseResult.Error.AddFailed(add.message, workOrderId)
            }

        }
    }

    sealed interface UseCaseResult {
        data class Success(
            val workOrderId: WorkOrderId,
            val outcome: AddOutcome,
        ) : UseCaseResult {
            sealed interface AddOutcome {
                data class Added(val invoiceNumber: InvoiceNumber) : AddOutcome
                data class Duplicate(val invoiceNumber: InvoiceNumber) : AddOutcome
            }
        }

        sealed class Error(open val message: String) : UseCaseResult {
            data object NoUser : Error("No user logged in")
            data object InvalidOrderId : Error("Invoice number cannot be empty.")
            data class EnsureFailed(override val message: String) : Error(message)
            data class PersistSelectionFailed(override val message: String) : Error(message)
            data class AddFailed(
                override val message: String,
                val workOrderId: WorkOrderId
            ) : Error(message)
        }
    }

}
