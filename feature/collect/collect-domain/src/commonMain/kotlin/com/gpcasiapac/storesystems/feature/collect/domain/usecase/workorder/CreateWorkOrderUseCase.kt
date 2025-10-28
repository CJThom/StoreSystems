package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectWorkOrder
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import java.util.UUID.randomUUID
import kotlin.time.Clock

class CreateWorkOrderUseCase(
    private val orderLocalRepository: OrderLocalRepository,
) {

    suspend operator fun invoke(userRefId: String): UseCaseResult {

        val workOrderId = WorkOrderId(randomUUID().toString()) // TODO: Use kotlin kmp
        val now = Clock.System.now()

        val newWorkOrder = CollectWorkOrder(
            workOrderId = workOrderId,
            userId = userRefId,
            createdAt = now,
            collectingType = null,
            courierName = null
        )

        orderLocalRepository.insertOrReplaceWorkOrder(newWorkOrder)

        // TODO: Handle exceptions
        return UseCaseResult.Success(workOrderId)

    }

    sealed interface UseCaseResult {
        data class Success(val workOrderId: WorkOrderId) : UseCaseResult
        sealed class Error(val message: String) : UseCaseResult {
            data class Unexpected(val reason: String) : Error(reason)
        }
    }

}
