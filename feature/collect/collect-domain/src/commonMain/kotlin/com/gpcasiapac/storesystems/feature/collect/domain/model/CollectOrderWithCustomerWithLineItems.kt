package com.gpcasiapac.storesystems.feature.collect.domain.model

data class CollectOrderWithCustomerWithLineItems(
    val order: CollectOrder,
    val customer: CollectOrderCustomer,
    val lineItemList: List<CollectOrderLineItem>
)