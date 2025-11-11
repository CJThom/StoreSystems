package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.WorkOrderWithOrderWithCustomersWithLineItemsRelation
import com.gpcasiapac.storesystems.feature.collect.domain.model.WorkOrderWithOrderWithCustomersWithLineItems

fun WorkOrderWithOrderWithCustomersWithLineItemsRelation.toDomain(): WorkOrderWithOrderWithCustomersWithLineItems {
    return WorkOrderWithOrderWithCustomersWithLineItems(
        collectWorkOrder = this.collectWorkOrderEntity.toDomain(),
        collectOrderWithCustomerWithLineItemsList = this.collectOrderWithCustomerWithLineItemsRelationList.toDomain()
    )
}

fun List<WorkOrderWithOrderWithCustomersWithLineItemsRelation>.toDomain(): List<WorkOrderWithOrderWithCustomersWithLineItems> {
    return map { it.toDomain() }
}