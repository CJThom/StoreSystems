package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectionTypeGating
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
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
 * - per-option collection type gating derived from current order composition
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
        val ordersFlow = observeWorkOrderItemsInScanOrderUseCase(workOrderId)
            .distinctUntilChanged()

        val workOrderFlow = observeCollectWorkOrderUseCase(workOrderId)
            .distinctUntilChanged()

        val signatureFlow = observeWorkOrderSignatureUseCase(workOrderId)
            .map { sig -> sig?.signatureBase64?.isNotBlank() == true }
            .distinctUntilChanged()

        return combine(ordersFlow, workOrderFlow, signatureFlow) { orders, wo, hasSignature ->
            val gating = deriveCollectionTypeGating(orders)
            FulfilmentGating(
                workOrderId = workOrderId,
                hasOrders = orders.isNotEmpty(),
                collectingType = wo?.collectingType,
                courierName = wo?.courierName,
                hasSignature = hasSignature,
                collectionTypeGating = gating,
            )
        }.distinctUntilChanged()
    }

    /** Pure function copied from ObserveCollectionTypeGatingUseCase to avoid extra stream in VM. */
    private fun deriveCollectionTypeGating(orderList: List<com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer>): CollectionTypeGating {
        if (orderList.isEmpty()) {
            return CollectionTypeGating(
                isStandardEnabled = false,
                isAccountEnabled = false,
                isCourierEnabled = false,
            )
        }
        val typeList: Set<CustomerType> = orderList.map { it.customer.customerType }.toSet()
        val hasB2C = typeList.contains(CustomerType.B2C)
        val hasB2B = typeList.contains(CustomerType.B2B)
        val standardEnabled = hasB2C && !hasB2B
        val accountEnabled = hasB2B && !hasB2C
        val courierEnabled = true // there is at least one order
        return CollectionTypeGating(
            isStandardEnabled = standardEnabled,
            isAccountEnabled = accountEnabled,
            isCourierEnabled = courierEnabled,
        )
    }
}
