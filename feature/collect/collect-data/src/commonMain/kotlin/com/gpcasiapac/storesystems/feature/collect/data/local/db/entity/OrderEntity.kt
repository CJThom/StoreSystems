package com.gpcasiapac.storesystems.feature.collect.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import kotlin.time.Instant

@Entity(tableName = "orders")
data class OrderEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "invoice_number")
    val invoiceNumber: String,

    @ColumnInfo(name = "web_order_number")
    val webOrderNumber: String?,

    @ColumnInfo(name = "picked_at")
    val pickedAt: Instant,

    @Embedded
    val customerEntity: CustomerEntity

)
