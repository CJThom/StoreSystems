package com.gpcasiapac.storesystems.feature.collect.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "collect_order_line_items",
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

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "line_number")
    val lineNumber: Int,

    @ColumnInfo(name = "invoice_number")
    val invoiceNumber: String

)
