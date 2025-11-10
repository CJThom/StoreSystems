package com.gpcasiapac.storesystems.core.sync_queue.api.model


import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class SyncTask(
    val id: String,
    val taskType: TaskType,
    val status: TaskStatus,
    val taskId: String, // References actual entity ID (invoice number, order ID, etc.)
    val noOfAttempts: Int,
    val maxAttempts: Int = 3,
    val priority: Int = 0, // Higher number = higher priority
    val addedTime: Instant,
    val updatedTime: Instant,
    val lastAttemptTime: Instant?,
    val errorAttempts: List<SyncTaskAttemptError> = emptyList(),
    // Human-readable submitter for UIs/search
    val submittedBy: String? = null,
    // Client-side request identifier associated with this task
    val requestId: String
)
