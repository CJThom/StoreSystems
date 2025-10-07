package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.OrderEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrder

fun OrderEntity.toDomain(): CollectOrder {
    return CollectOrder(
        id = this.id,
        invoiceNumber = this.invoiceNumber,
        webOrderNumber = this.webOrderNumber,
        pickedAt = this.pickedAt,
        collectOrderCustomer = CollectOrderCustomer(
            customerNumber = this.customerEntity.customerNumber,
            customerType = this.customerEntity.customerType,
            accountName = this.customerEntity.accountName,
            firstName = this.customerEntity.firstName,
            lastName = this.customerEntity.lastName,
            phone = this.customerEntity.phone,
        ),
    )
}

fun List<OrderEntity>.toDomain(): List<CollectOrder> {
    return this.map { it.toDomain() }
}
