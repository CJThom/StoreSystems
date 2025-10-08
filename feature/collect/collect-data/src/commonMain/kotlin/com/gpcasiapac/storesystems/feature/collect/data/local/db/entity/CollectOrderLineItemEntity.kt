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
    @ColumnInfo(name = "product_number")
    val productNumber: String,
    @ColumnInfo(name = "product_description")
    val productDescription: String,
    @ColumnInfo(name = "quantity")
    val quantity: Int,
    @ColumnInfo(name = "unit_price")
    val unitPrice: Double
)
