package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.relation.CollectOrderWithCustomerWithLineItemsRelation
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems

internal fun CollectOrderWithCustomerWithLineItemsRelation.toDomain(): CollectOrderWithCustomerWithLineItems {
    return CollectOrderWithCustomerWithLineItems(
        order = orderEntity.toDomain(),
        customer = customerEntity.toDomain(),
        lineItemList = lineItemEntityList.toDomain()
    )
}

internal fun List<CollectOrderWithCustomerWithLineItemsRelation>.toDomain(): List<CollectOrderWithCustomerWithLineItems> {
    return map { it.toDomain() }
}
