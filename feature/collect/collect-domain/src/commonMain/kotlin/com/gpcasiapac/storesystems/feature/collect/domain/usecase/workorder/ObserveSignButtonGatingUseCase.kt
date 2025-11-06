package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.SignButtonGating
import com.gpcasiapac.storesystems.feature.collect.domain.model.SignButtonInputs
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.util.deriveSignButtonGating
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Observes inputs required for Sign button gating from domain sources and maps into SignButtonGating.
 * Note: The ID verification selection is a presentation concern today, so the resulting gating does
 * not include that flag. The ViewModel should combine this result with its own state to finalize
 * enablement (see presentation wiring).
 */
class ObserveSignButtonGatingUseCase(
    private val observeWorkOrderItemsInScanOrder: ObserveWorkOrderItemsInScanOrderUseCase,
    private val observeCollectWorkOrder: ObserveCollectWorkOrderUseCase,
) {
    operator fun invoke(workOrderId: WorkOrderId): Flow<SignButtonGating> {
        val ordersCountFlow = observeWorkOrderItemsInScanOrder(workOrderId)
            .map { it.size }
            .distinctUntilChanged()

        val workOrderFlow = observeCollectWorkOrder(workOrderId)
            .distinctUntilChanged()

        return combine(ordersCountFlow, workOrderFlow) { count, wo ->
            val inputs = SignButtonInputs(
                ordersCount = count,
                collectingType = wo?.collectingType,
                courierName = wo?.courierName,
                isIdVerificationSelected = true // placeholder true: presentation will enforce actual flag
            )
            deriveSignButtonGating(inputs)
        }.distinctUntilChanged()
    }
}
