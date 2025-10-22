package com.gpcasiapac.storesystems.core.sync_queue.api.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
data class SyncTaskAttemptError(
    val attemptNumber: Int,
    @Contextual val timestamp: kotlin.time.Instant,
    val errorMessage: String
)
