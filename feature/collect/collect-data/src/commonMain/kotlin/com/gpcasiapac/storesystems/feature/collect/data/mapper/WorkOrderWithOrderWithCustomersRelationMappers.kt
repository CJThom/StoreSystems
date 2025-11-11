package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.WorkOrderWithOrderWithCustomersRelation
import com.gpcasiapac.storesystems.feature.collect.domain.model.WorkOrderWithOrderWithCustomers

fun WorkOrderWithOrderWithCustomersRelation.toDomain(): WorkOrderWithOrderWithCustomers {
    return WorkOrderWithOrderWithCustomers(
        collectWorkOrder = this.collectWorkOrderEntity.toDomain(),
        collectOrderWithCustomerList = this.collectOrderWithCustomerRelation.toDomain()
    )
}

fun List<WorkOrderWithOrderWithCustomersRelation>.toDomain(): List<WorkOrderWithOrderWithCustomers> {
    return map { it.toDomain() }
}