package com.gpcasiapac.storesystems.core.sync_queue.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gpcasiapac.storesystems.core.sync_queue.domain.model.SyncTaskAttemptError
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
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
    val addedTime: kotlin.time.Instant,

    @ColumnInfo(name = "updated_time")
    val updatedTime: kotlin.time.Instant,

    @ColumnInfo(name = "last_attempt_time")
    val lastAttemptTime: kotlin.time.Instant?,

    @ColumnInfo(name = "error_attempts")
    val errorAttempts: List<SyncTaskAttemptError>? // Room handles JSON conversion with TypeConverter
)