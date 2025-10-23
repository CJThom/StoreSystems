package com.gpcasiapac.storesystems.feature.collect.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "work_order_items",
    primaryKeys = ["work_order_id", "invoice_number"],
    foreignKeys = [
        ForeignKey(
            entity = CollectWorkOrderEntity::class,
            parentColumns = ["work_order_id"],
            childColumns = ["work_order_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CollectOrderEntity::class,
            parentColumns = ["invoice_number"],
            childColumns = ["invoice_number"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["invoice_number"]),
        Index(value = ["work_order_id", "position"])
    ]
)
data class CollectWorkOrderItemEntity(

    @ColumnInfo(name = "work_order_id")
    val workOrderId: String,

    @ColumnInfo(name = "invoice_number")
    val invoiceNumber: String,

    // Scan/insertion order within a work order (1-based)
    @ColumnInfo(name = "position")
    val position: Long,

)