package com.gpcasiapac.storesystems.core.sync_queue.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
data class SyncTaskAttemptError(
    val attemptNumber: Int,
    @Contextual val timestamp: Instant,
    val errorMessage: String
)