package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.OrderEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.Customer
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order

fun OrderEntity.toDomain(): Order {
    return Order(
        id = this.id,
        customerType = this.customerEntity.customerType,
        accountName = this.customerEntity.accountName,
        invoiceNumber = this.invoiceNumber,
        webOrderNumber = this.webOrderNumber,
        pickedAt = this.pickedAt,
        customer = Customer(
            customerNumber = this.customerEntity.customerNumber,
            customerType = this.customerEntity.customerType,
            accountName = this.customerEntity.accountName,
            firstName = this.customerEntity.firstName,
            lastName = this.customerEntity.lastName,
            phone = this.customerEntity.phone,
        ),
    )
}

fun List<OrderEntity>.toDomain(): List<Order> {
    return this.map { it.toDomain() }
}
