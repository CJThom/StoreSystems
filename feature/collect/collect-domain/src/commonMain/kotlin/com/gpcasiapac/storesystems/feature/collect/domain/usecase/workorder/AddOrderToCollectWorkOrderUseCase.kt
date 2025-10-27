package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.CancellationException

// TODO: split into seperate usecases ie. scanning
class AddOrderToCollectWorkOrderUseCase(
    private val orderRepository: OrderRepository,
) {

    suspend operator fun invoke(workOrderId: WorkOrderId, orderId: String): UseCaseResult {
        return try {
            val added = orderRepository.addOrderToCollectWorkOrder(workOrderId, orderId)
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

//
//class AddOrderSelectionUseCase(
//    private val orderRepository: OrderRepository,
//) {
//
//    suspend operator fun invoke(orderId: String, userRefId: String): UseCaseResult {
//        val clean = orderId.trim()
//        if (clean.isEmpty()) return UseCaseResult.Error.InvalidInput
//        return try {
//            val added = orderRepository.addSelectedId(clean, userRefId)
//            if (added) UseCaseResult.Added(clean) else UseCaseResult.Duplicate(clean)
//        } catch (ce: CancellationException) {
//            throw ce // never swallow coroutine cancellation
//        } catch (t: Throwable) {
//            UseCaseResult.Error.Unexpected(t.message ?: "Failed to add order to work order.")
//        }
//    }
//
//    sealed interface UseCaseResult {
//        data class Added(val invoiceNumber: String) : UseCaseResult
//        data class Duplicate(val invoiceNumber: String) : UseCaseResult
//        sealed class Error(val message: String) : UseCaseResult {
//            data object InvalidInput : Error("Invoice number cannot be empty.")
//            data class Unexpected(val reason: String) : Error(reason)
//        }
//    }
//}
//
