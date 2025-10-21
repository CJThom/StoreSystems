package com.gpcasiapac.storesystems.feature.collect.data.local.db.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderItemEntity

data class WorkOrderWithOrderWithCustomersRelation(

    @Embedded
    val collectWorkOrderEntity: CollectWorkOrderEntity,

    @Relation(
        parentColumn = "work_order_id",
        entityColumn = "invoice_number",
        associateBy = Junction(CollectWorkOrderItemEntity::class),
        entity = CollectOrderEntity::class
    )
    val collectOrderWithCustomerRelation: List<CollectOrderWithCustomerRelation>

)