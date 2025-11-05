package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderCustomerEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderCustomer

fun CollectOrderCustomerEntity.toDomain(): CollectOrderCustomer {
    return CollectOrderCustomer(
        invoiceNumber = this.invoiceNumber,
        number = this.number,
        name = this.name,
        phone = this.phone,
        customerType = this.customerType
    )
}

fun List<CollectOrderCustomerEntity>.toDomain(): List<CollectOrderCustomer> {
    return this.map { it.toDomain() }
}