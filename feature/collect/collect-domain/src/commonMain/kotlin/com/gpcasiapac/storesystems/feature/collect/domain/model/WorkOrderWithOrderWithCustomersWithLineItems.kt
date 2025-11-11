package com.gpcasiapac.storesystems.feature.collect.domain.model

data class WorkOrderWithOrderWithCustomersWithLineItems(
    val collectWorkOrder: CollectWorkOrder,
    val collectOrderWithCustomerWithLineItemsList: List<CollectOrderWithCustomerWithLineItems>
)