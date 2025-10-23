package com.gpcasiapac.storesystems.feature.collect.data.local.db.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderItemEntity

/**
 * Relation anchored on a work_order_items row and its nested order+customer relation.
 * Lets us fetch items ordered by [CollectWorkOrderItemEntity.position] and have Room
 * materialize the nested CollectOrderWithCustomerRelation for each item.
 */
data class WorkOrderItemWithOrderWithCustomerRelation(
    @Embedded val item: CollectWorkOrderItemEntity,

    @Relation(
        parentColumn = "invoice_number",
        entityColumn = "invoice_number",
        entity = CollectOrderEntity::class
    )
    val orderWithCustomer: CollectOrderWithCustomerRelation,
)
