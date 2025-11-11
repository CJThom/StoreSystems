package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectWorkOrder
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import java.util.UUID.randomUUID
import kotlin.time.Clock

class CreateWorkOrderUseCase(
    private val orderLocalRepository: OrderLocalRepository,
) {

    suspend operator fun invoke(userId: UserId): UseCaseResult {

        val workOrderId = WorkOrderId(randomUUID().toString()) // TODO: Use kotlin kmp
        val now = Clock.System.now()

        val newWorkOrder = CollectWorkOrder(
            workOrderId = workOrderId,
            userId = userId,
            createdAt = now,
            collectingType = null,
            courierName = null,
            idVerified = false,
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
