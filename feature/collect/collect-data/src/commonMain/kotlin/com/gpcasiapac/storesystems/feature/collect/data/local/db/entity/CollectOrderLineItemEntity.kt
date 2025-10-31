package com.gpcasiapac.storesystems.feature.collect.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "collect_order_line_items",
    primaryKeys = ["invoice_number", "line_number"],
    foreignKeys = [
        ForeignKey(
            entity = CollectOrderEntity::class,
            parentColumns = ["invoice_number"],
            childColumns = ["invoice_number"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CollectOrderLineItemEntity(

    @ColumnInfo(name = "line_number")
    val lineNumber: Int,

    @ColumnInfo(name = "invoice_number", index = true)
    val invoiceNumber: String,

    @ColumnInfo(name = "sku")
    val sku: String,

    @ColumnInfo(name = "barcode")
    val barcode: String?,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "quantity")
    val quantity: Int,

    @ColumnInfo(name = "image_url")
    val imageUrl: String?,

)
