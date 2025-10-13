package com.gpcasiapac.storesystems.feature.collect.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "collect_work_orders",
    primaryKeys = ["user_id", "invoice_number"],
    foreignKeys = [
        ForeignKey(
            entity = CollectOrderEntity::class,
            parentColumns = ["invoice_number"],
            childColumns = ["invoice_number"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["invoice_number"])]
)
data class CollectWorkOrderEntity(
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "invoice_number")
    val invoiceNumber: String
)
