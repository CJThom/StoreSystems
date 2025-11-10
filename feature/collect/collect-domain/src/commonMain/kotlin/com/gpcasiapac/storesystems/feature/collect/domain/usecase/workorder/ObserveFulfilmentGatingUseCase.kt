package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.FulfilmentGating
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Single source of truth for cohesive fulfilment gating facts:
 * - whether there are any orders
 * - selected collecting type and courier name (from Work Order)
 * - whether a signature is present
 *
 * Presentation layer can further derive enabled/disabled states from these facts
 * (e.g., ID verification checkbox for standard/account flows).
 */
class ObserveFulfilmentGatingUseCase(
    private val observeWorkOrderItemsInScanOrderUseCase: ObserveWorkOrderItemsInScanOrderUseCase,
    private val observeCollectWorkOrderUseCase: ObserveCollectWorkOrderUseCase,
    private val observeWorkOrderSignatureUseCase: ObserveWorkOrderSignatureUseCase,
) {

    operator fun invoke(workOrderId: WorkOrderId): Flow<FulfilmentGating> {
        val ordersCountFlow = observeWorkOrderItemsInScanOrderUseCase(workOrderId)
            .map { it.size }
            .distinctUntilChanged()

        val workOrderFlow = observeCollectWorkOrderUseCase(workOrderId)
            .distinctUntilChanged()

        val signatureFlow = observeWorkOrderSignatureUseCase(workOrderId)
            .map { sig -> sig?.signatureBase64?.isNotBlank() == true }
            .distinctUntilChanged()

        return combine(ordersCountFlow, workOrderFlow, signatureFlow) { count, wo, hasSignature ->
            FulfilmentGating(
                workOrderId = workOrderId,
                hasOrders = count > 0,
                collectingType = wo?.collectingType,
                courierName = wo?.courierName,
                hasSignature = hasSignature,
            )
        }.distinctUntilChanged()
    }
}
