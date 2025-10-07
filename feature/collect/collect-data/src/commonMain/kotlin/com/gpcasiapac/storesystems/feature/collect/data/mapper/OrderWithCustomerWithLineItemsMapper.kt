package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.OrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems

internal fun OrderWithCustomerWithLineItems.toDomain(): CollectOrderWithCustomerWithLineItems {
    return CollectOrderWithCustomerWithLineItems(
        order = order.toDomain(),
        customer = customer.toDomain(),
        lineItemList = lineItems.toDomain()
    )
}

internal fun List<OrderWithCustomerWithLineItems>.toDomain(): List<CollectOrderWithCustomerWithLineItems> {
    return map { it.toDomain() }
}
