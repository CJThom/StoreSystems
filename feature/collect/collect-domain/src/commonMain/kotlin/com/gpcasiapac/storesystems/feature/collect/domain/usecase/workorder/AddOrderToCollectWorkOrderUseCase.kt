package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectWorkOrderItem
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import kotlinx.coroutines.CancellationException

// TODO: split into seperate usecases ie. scanning
class AddOrderToCollectWorkOrderUseCase(
    private val orderLocalRepository: OrderLocalRepository,
    private val createWorkOrderUseCase: CreateWorkOrderUseCase
) {

    suspend operator fun invoke(workOrderId: WorkOrderId, orderId: String): UseCaseResult {
        return try {
            val added: Boolean = orderLocalRepository.write {
                val workOrderId: WorkOrderId? = when (val result =
                    createWorkOrderUseCase(userId = UserId("demo"))) {
                    is CreateWorkOrderUseCase.UseCaseResult.Error.Unexpected -> {
                        null
                        //    UseCaseResult.Error.Unexpected(result.reason)
                    }

                    is CreateWorkOrderUseCase.UseCaseResult.Success -> {
                        result.workOrderId
                    }
                }
                if (workOrderId == null) return@write false
                val nextPosition = orderLocalRepository.getMaxWorkOrderItemPosition(workOrderId) + 1
                val item = CollectWorkOrderItem(
                    workOrderId = workOrderId,
                    invoiceNumber = orderId,
                    position = nextPosition,
                )
                orderLocalRepository.insertWorkOrderItem(item)
            }
            if (added) UseCaseResult.Added(orderId) else UseCaseResult.Duplicate(orderId)
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

