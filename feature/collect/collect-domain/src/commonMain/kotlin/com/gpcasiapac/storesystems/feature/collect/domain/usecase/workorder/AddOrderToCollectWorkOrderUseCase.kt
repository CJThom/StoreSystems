package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectWorkOrderItem
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import kotlinx.coroutines.CancellationException

// TODO: split into seperate usecases ie. scanning
class AddOrderToCollectWorkOrderUseCase(
    private val orderLocalRepository: OrderLocalRepository,
) {

    suspend operator fun invoke(workOrderId: WorkOrderId, orderId: String): UseCaseResult {
        if (orderId.isBlank()) return UseCaseResult.Error.InvalidInput
        return try {
            val rowId: Long = orderLocalRepository.write {
                val nextPosition = orderLocalRepository.getMaxWorkOrderItemPosition(workOrderId) + 1
                val item = CollectWorkOrderItem(
                    workOrderId = workOrderId,
                    invoiceNumber = orderId,
                    position = nextPosition,
                )
                orderLocalRepository.insertWorkOrderItem(item)
            }
            if (rowId != -1L) {
                UseCaseResult.Added(orderId)
            } else {
                UseCaseResult.Duplicate(orderId)
            }
        } catch (ce: CancellationException) {
            throw ce // never swallow coroutine cancellation
        } catch (t: Throwable) {
            UseCaseResult.Error.Unexpected(t.message ?: "Failed to add order to work order.")
        }
    }

    sealed interface UseCaseResult {
        data class Added(val invoiceNumber: String) : UseCaseResult
        data class Duplicate(val invoiceNumber: String) : UseCaseResult
        sealed class Error(val message: String) : UseCaseResult {
            data object InvalidInput : Error("Invoice number cannot be empty.")
            data class Unexpected(val reason: String) : Error(reason)
        }
    }

}

