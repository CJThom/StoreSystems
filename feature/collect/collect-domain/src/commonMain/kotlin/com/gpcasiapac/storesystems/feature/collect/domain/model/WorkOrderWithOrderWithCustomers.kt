package com.gpcasiapac.storesystems.feature.collect.domain.model


data class WorkOrderWithOrderWithCustomers(
    val collectWorkOrder: CollectWorkOrder,
    val collectOrderWithCustomerList: List<CollectOrderWithCustomer>
)