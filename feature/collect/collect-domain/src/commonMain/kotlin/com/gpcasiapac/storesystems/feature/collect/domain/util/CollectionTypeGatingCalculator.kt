package com.gpcasiapac.storesystems.feature.collect.domain.util

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectionTypeGating
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType

/**
 * Pure function to derive which collection types are enabled from the current orders list.
 * Rules:
 * - Standard enabled iff all orders are B2C and there is at least one order.
 * - Account enabled iff all orders are B2B and there is at least one order.
 * - Courier enabled iff there is at least one order (any mix allowed).
 * - If any order has missing/unknown type (should not happen with current model), treat as mixed.
 */
fun deriveCollectionTypeGating(orders: List<CollectOrderWithCustomer>): CollectionTypeGating {
    if (orders.isEmpty()) {
        return CollectionTypeGating(false, false, false, null)
    }

    // Determine composition
    val types: Set<CustomerType> = orders.map { it.customer.customerType }.toSet()

    val hasB2C = types.contains(CustomerType.B2C)
    val hasB2B = types.contains(CustomerType.B2B)

    val standardEnabled = hasB2C && !hasB2B
    val accountEnabled = hasB2B && !hasB2C
    val courierEnabled = true // there is at least one order, so always true in non-empty case

    val enabledCount = listOf(standardEnabled, accountEnabled, courierEnabled).count { it }
    val defaultSelection = when (enabledCount) {
        1 -> when {
            standardEnabled -> CollectingType.STANDARD
            accountEnabled -> CollectingType.ACCOUNT
            courierEnabled -> CollectingType.COURIER
            else -> null
        }
        else -> null
    }

    return CollectionTypeGating(
        isStandardEnabled = standardEnabled,
        isAccountEnabled = accountEnabled,
        isCourierEnabled = courierEnabled,
        defaultSelection = defaultSelection
    )
}