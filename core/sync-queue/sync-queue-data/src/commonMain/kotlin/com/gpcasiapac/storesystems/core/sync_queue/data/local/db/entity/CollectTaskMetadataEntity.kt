@file:OptIn(ExperimentalTime::class)

package com.gpcasiapac.storesystems.core.sync_queue.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Room entity for collect task metadata.
 * Stores order and customer information for collect tasks.
 */
@Entity(
    tableName = "collect_task_metadata",
    foreignKeys = [
        ForeignKey(
            entity = SyncTaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["sync_task_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sync_task_id"], unique = false)]
)
data class CollectTaskMetadataEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "sync_task_id")
    val syncTaskId: String,
    
    // Order fields
    @ColumnInfo(name = "invoice_number")
    val invoiceNumber: String,
    
    @ColumnInfo(name = "sales_order_number")
    val salesOrderNumber: String,
    
    @ColumnInfo(name = "web_order_number")
    val webOrderNumber: String?,
    
    @ColumnInfo(name = "order_created_at")
    val orderCreatedAt: Instant,
    
    @ColumnInfo(name = "order_picked_at")
    val orderPickedAt: Instant,
    
    // Customer fields
    @ColumnInfo(name = "customer_number")
    val customerNumber: String,
    
    @ColumnInfo(name = "customer_type")
    val customerType: String, // "B2B" or "B2C"
    
    @ColumnInfo(name = "account_name")
    val accountName: String?,
    
    @ColumnInfo(name = "first_name")
    val firstName: String?,
    
    @ColumnInfo(name = "last_name")
    val lastName: String?,
    
    @ColumnInfo(name = "phone")
    val phone: String?
)
