package com.gpcasiapac.storesystems.feature.collect.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber

@Entity(tableName = "collect_order_customers")
data class CollectOrderCustomerEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "invoice_number")
    val invoiceNumber: InvoiceNumber,

    @ColumnInfo(name = "number")
    val number: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "phone")
    val phone: String?,

    @ColumnInfo(name = "customer_type")
    val customerType: CustomerType

)
