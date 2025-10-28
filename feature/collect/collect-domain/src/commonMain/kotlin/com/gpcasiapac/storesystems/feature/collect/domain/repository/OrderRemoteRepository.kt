package com.gpcasiapac.storesystems.feature.collect.domain.repository

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems

/** Remote (network-only) repository facade for the Collect feature. */
interface OrderRemoteRepository {
    /** Fetch orders from the backend as domain models. */
    suspend fun fetchOrders(): List<CollectOrderWithCustomerWithLineItems>
}
