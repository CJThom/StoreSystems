package com.gpcasiapac.storesystems.core.sync_queue.api.model

import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class SyncTaskAttemptError(
    val attemptNumber: Int,
    val timestamp: kotlin.time.Instant,
    val errorMessage: String
)
