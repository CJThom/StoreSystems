package com.gpcasiapac.storesystems.feature.collect.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderChannel
import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import kotlin.time.Instant

@Entity(tableName = "collect_orders")
data class CollectOrderEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "invoice_number")
    val invoiceNumber: InvoiceNumber,

    // Optional backend id (not PK), stored for traceability
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "order_number")
    val orderNumber: String,

    @ColumnInfo(name = "web_order_number")
    val webOrderNumber: String?,

    // New fields per updated backend structure
    @ColumnInfo(name = "customer_type")
    val orderChannel: OrderChannel,

    @ColumnInfo(name = "invoice_date_time")
    val invoiceDateTime: Instant,

    @ColumnInfo(name = "created_date_time")
    val createdDateTime: Instant,

    @ColumnInfo(name = "is_locked")
    val isLocked: Boolean,

    @ColumnInfo(name = "locked_by")
    val lockedBy: String?,

    @ColumnInfo(name = "locked_date_time")
    val lockedDateTime: Instant?,

    )
