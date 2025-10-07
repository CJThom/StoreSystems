package com.gpcasiapac.storesystems.feature.collect.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType

@Entity(tableName = "collect_order_customers")
data class CollectOrderCustomerEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "invoice_number")
    val invoiceNumber: String,

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
