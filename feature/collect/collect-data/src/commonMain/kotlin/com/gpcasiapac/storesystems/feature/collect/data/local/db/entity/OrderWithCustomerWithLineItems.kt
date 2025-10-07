package com.gpcasiapac.storesystems.feature.collect.data.local.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class OrderWithCustomerWithLineItems(
    @Embedded
    val order: OrderEntity,
    @Relation(
        parentColumn = "customer_id",
        entityColumn = "id"
    )
    val customer: CustomerEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "order_id"
    )
    val lineItems: List<OrderLineItemEntity>
)
