package com.gpcasiapac.storesystems.feature.collect.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import kotlin.time.Instant

@Entity(
    tableName = "work_orders",
    indices = [
        Index(value = ["user_id"])
    ]
)
data class CollectWorkOrderEntity(

    @PrimaryKey
    @ColumnInfo(name = "work_order_id")
    val workOrderId: WorkOrderId,

    @ColumnInfo(name = "user_id")
    val userId: UserId,

    @ColumnInfo(name = "created_at")
    val createdAt: Instant,

    // New: persist collecting type as enum in DB (Room supports enums)
    @ColumnInfo(name = "collecting_type")
    val collectingType: CollectingType?,

    // New: persisted courier name for COURIER collecting type
    @ColumnInfo(name = "courier_name")
    val courierName: String?

)