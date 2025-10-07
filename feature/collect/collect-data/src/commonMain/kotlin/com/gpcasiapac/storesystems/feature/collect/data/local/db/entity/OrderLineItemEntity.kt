package com.gpcasiapac.storesystems.feature.collect.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_line_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["order_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OrderLineItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val order_id: String,
    val lineNumber: Int,
)
