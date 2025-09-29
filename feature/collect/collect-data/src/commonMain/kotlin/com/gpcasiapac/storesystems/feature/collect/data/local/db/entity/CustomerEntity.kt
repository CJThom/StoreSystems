package com.gpcasiapac.storesystems.feature.collect.data.local.db.entity

import androidx.room.ColumnInfo
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType

data class CustomerEntity(

    @ColumnInfo(name = "customer_number")
    val customerNumber: String,

    @ColumnInfo(name = "customer_type")
    val customerType: CustomerType,

    @ColumnInfo(name = "account_name")
    val accountName: String?,

    @ColumnInfo(name = "first_name")
    val firstName: String?,

    @ColumnInfo(name = "last_name")
    val lastName: String?,

    @ColumnInfo(name = "phone")
    val phone: String?

)
