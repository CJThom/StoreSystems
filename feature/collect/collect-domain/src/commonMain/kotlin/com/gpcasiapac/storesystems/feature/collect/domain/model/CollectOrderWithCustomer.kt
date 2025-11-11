package com.gpcasiapac.storesystems.feature.collect.domain.model

data class CollectOrderWithCustomer(
    val order: CollectOrder,
    val customer: CollectOrderCustomer
)