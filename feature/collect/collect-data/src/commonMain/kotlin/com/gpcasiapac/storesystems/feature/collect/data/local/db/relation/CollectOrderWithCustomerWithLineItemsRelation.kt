package com.gpcasiapac.storesystems.feature.collect.data.local.db.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderCustomerEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderLineItemEntity

data class CollectOrderWithCustomerWithLineItemsRelation(

    @Embedded
    val orderEntity: CollectOrderEntity,

    @Relation(
        parentColumn = "invoice_number",
        entityColumn = "invoice_number"
    )
    val customerEntity: CollectOrderCustomerEntity,

    @Relation(
        parentColumn = "invoice_number",
        entityColumn = "invoice_number"
    )
    val lineItemEntityList: List<CollectOrderLineItemEntity>

)