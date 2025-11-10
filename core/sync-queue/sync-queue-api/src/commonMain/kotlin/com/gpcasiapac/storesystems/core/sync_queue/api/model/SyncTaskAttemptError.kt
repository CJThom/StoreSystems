package com.gpcasiapac.storesystems.core.sync_queue.api.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class SyncTaskAttemptError(
    val attemptNumber: Int,
    val timestamp: Instant,
    val errorMessage: String
)
