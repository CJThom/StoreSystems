@file:OptIn(ExperimentalTime::class)

package com.gpcasiapac.storesystems.core.sync_queue.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Entity(tableName = "sync_tasks")
data class SyncTaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "task_type")
    val taskType: String,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "task_id")
    val taskId: String,

    @ColumnInfo(name = "no_of_attempts")
    val noOfAttempts: Int,

    @ColumnInfo(name = "max_attempts")
    val maxAttempts: Int,

    @ColumnInfo(name = "priority")
    val priority: Int,

    @ColumnInfo(name = "added_time")
    val addedTime: Instant,

    @ColumnInfo(name = "updated_time")
    val updatedTime: Instant,

    @ColumnInfo(name = "last_attempt_time")
    val lastAttemptTime: Instant?,

    @ColumnInfo(name = "error_attempts")
    val errorAttempts: String? // Store as JSON string
)