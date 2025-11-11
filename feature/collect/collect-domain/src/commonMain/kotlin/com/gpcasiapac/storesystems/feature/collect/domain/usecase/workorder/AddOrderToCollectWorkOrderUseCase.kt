package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectWorkOrderItem
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import kotlinx.coroutines.CancellationException

@Deprecated("Use ApplyOrderSelectionDeltaUseCase()")
class AddOrderToCollectWorkOrderUseCase(
    private val orderLocalRepository: OrderLocalRepository,
) {

    suspend operator fun invoke(workOrderId: WorkOrderId, invoiceNumber: InvoiceNumber): UseCaseResult {

        return try {
            val rowId: Long = orderLocalRepository.write {
                val nextPosition = orderLocalRepository.getMaxWorkOrderItemPosition(workOrderId) + 1
                val item = CollectWorkOrderItem(
                    workOrderId = workOrderId,
                    invoiceNumber = invoiceNumber,
                    position = nextPosition,
                )
                orderLocalRepository.insertWorkOrderItem(item)
            }
            if (rowId != -1L) {
                UseCaseResult.Added(invoiceNumber)
            } else {
                UseCaseResult.Duplicate(invoiceNumber)
            }
        } catch (ce: CancellationException) {
            throw ce // never swallow coroutine cancellation
        } catch (t: Throwable) {
            UseCaseResult.Error.Unexpected(t.message ?: "Failed to add order to work order.")
        }
    }

    sealed interface UseCaseResult {
        data class Added(val invoiceNumber: InvoiceNumber) : UseCaseResult
        data class Duplicate(val invoiceNumber: InvoiceNumber) : UseCaseResult
        sealed class Error(val message: String) : UseCaseResult {
            data object InvalidInput : Error("Invoice number cannot be empty.")
            data class Unexpected(val reason: String) : Error(reason)
        }
    }

}

