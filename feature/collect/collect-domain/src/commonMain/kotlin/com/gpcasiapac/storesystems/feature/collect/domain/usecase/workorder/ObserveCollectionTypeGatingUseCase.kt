package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectionTypeGating
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ObserveCollectionTypeGatingUseCase(
    private val observeWorkOrderItemsInScanOrderUseCase: ObserveWorkOrderItemsInScanOrderUseCase,
) {

    operator fun invoke(workOrderId: WorkOrderId): Flow<CollectionTypeGating> {
        return observeWorkOrderItemsInScanOrderUseCase(workOrderId)
            .map { orders -> deriveCollectionTypeGating(orders) }
            .distinctUntilChanged()
    }

    /**
     * Pure function to derive which collection types are enabled from the current orders list.
     * Rules:
     * - Standard enabled iff all orders are B2C and there is at least one order.
     * - Account enabled iff all orders are B2B and there is at least one order.
     * - Courier enabled iff there is at least one order (any mix allowed).
     * - If any order has missing/unknown type (should not happen with current model), treat as mixed.
     */
    private fun deriveCollectionTypeGating(orderList: List<CollectOrderWithCustomer>): CollectionTypeGating {

        if (orderList.isEmpty()) {
            return CollectionTypeGating(
                isStandardEnabled = false,
                isAccountEnabled = false,
                isCourierEnabled = false,
            )
        }

        // Determine composition
        val typeList: Set<CustomerType> = orderList.map { it.customer.customerType }.toSet()

        val hasB2C = typeList.contains(CustomerType.B2C)
        val hasB2B = typeList.contains(CustomerType.B2B)

        val standardEnabled = hasB2C && !hasB2B
        val accountEnabled = hasB2B && !hasB2C
        val courierEnabled = true // there is at least one order, so always true in non-empty case

        return CollectionTypeGating(
            isStandardEnabled = standardEnabled,
            isAccountEnabled = accountEnabled,
            isCourierEnabled = courierEnabled,
        )

    }

}

