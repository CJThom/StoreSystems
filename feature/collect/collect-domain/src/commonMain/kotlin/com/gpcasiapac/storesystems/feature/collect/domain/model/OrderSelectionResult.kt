package com.gpcasiapac.storesystems.feature.collect.domain.model

sealed interface OrderSelectionResult {
    data class Single(val order: CollectOrderWithCustomerWithLineItems?) : OrderSelectionResult
    data class Multi(val orderList: List<CollectOrderWithCustomer>) : OrderSelectionResult
}
