package com.gpcasiapac.storesystems.feature.collect.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import kotlin.time.Instant

@Entity(
    tableName = "signatures",
    foreignKeys = [
        ForeignKey(
            entity = CollectWorkOrderEntity::class,
            parentColumns = ["work_order_id"],
            childColumns = ["work_order_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
            deferred = false
        )
    ]
)
data class SignatureEntity(
    // 1:1 relation: use workOrderId as the primary key
    @PrimaryKey
    @ColumnInfo(name = "work_order_id")
    val workOrderId: WorkOrderId,

    // Base64-encoded PNG/JPEG string (keeping compatibility with current app flow)
    @ColumnInfo(name = "signature")
    val signatureBase64: String,

    @ColumnInfo(name = "signed_at")
    val signedAt: Instant,

    @ColumnInfo(name = "signed_by_name")
    val signedByName: String?,
)