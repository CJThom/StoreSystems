package com.gpcasiapac.storesystems.feature.collect.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import kotlin.time.Instant

@Entity(tableName = "orders")
data class OrderEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "customer_type")
    val customerType: CustomerType,

    @ColumnInfo(name = "customer_name")
    val customerName: String,

    @ColumnInfo(name = "invoice_number")
    val invoiceNumber: String,

    @ColumnInfo(name = "web_order_number")
    val webOrderNumber: String?,

    @ColumnInfo(name = "picked_at")
    val pickedAt: Instant,

)
