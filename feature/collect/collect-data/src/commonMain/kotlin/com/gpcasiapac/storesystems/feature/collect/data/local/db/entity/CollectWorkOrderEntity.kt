package com.gpcasiapac.storesystems.feature.collect.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import kotlin.time.Instant

@Entity(
    tableName = "work_orders",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["status"])
    ]
)
data class CollectWorkOrderEntity(

    @PrimaryKey
    @ColumnInfo(name = "work_order_id")
    val workOrderId: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "status")
    val status: String = "OPEN", // TODO: remove?

    @ColumnInfo(name = "created_at")
    val createdAt: Instant,

    // New: persist collecting type as enum in DB (Room supports enums)
    @ColumnInfo(name = "collecting_type")
    val collectingType: CollectingType = CollectingType.STANDARD,

    // New: persisted courier name for COURIER collecting type
    @ColumnInfo(name = "courier_name")
    val courierName: String = "",

    @ColumnInfo(name = "submitted_at")
    val submittedAt: Instant?, // TODO: remove?

    @ColumnInfo(name = "signature")
    val signature: String?,

    @ColumnInfo(name = "signed_at")
    val signedAt: Instant?,

    @ColumnInfo(name = "signed_by_name")
    val signedByName: String? // TODO: Rename to customer/representative?
)