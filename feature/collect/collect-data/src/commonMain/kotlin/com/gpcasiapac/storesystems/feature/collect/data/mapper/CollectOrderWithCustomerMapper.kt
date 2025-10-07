package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.CollectOrderWithCustomerRelation
import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.CollectOrderWithCustomerWithLineItemsRelation
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems

internal fun CollectOrderWithCustomerRelation.toDomain(): CollectOrderWithCustomer {
    return CollectOrderWithCustomer(
        order = orderEntity.toDomain(),
        customer = customerEntity.toDomain()
    )
}

internal fun List<CollectOrderWithCustomerRelation>.toDomain(): List<CollectOrderWithCustomer> {
    return map { it.toDomain() }
}
