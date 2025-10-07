package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderCustomerEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderCustomer

fun CollectOrderCustomerEntity.toDomain(): CollectOrderCustomer {
    return CollectOrderCustomer(
        customerNumber = this.customerNumber,
        customerType = this.customerType,
        accountName = this.accountName,
        firstName = this.firstName,
        lastName = this.lastName,
        phone = this.phone,
    )
}

fun List<CollectOrderCustomerEntity>.toDomain(): List<CollectOrderCustomer> {
    return this.map { it.toDomain() }
}
