package com.gpcasiapac.storesystems.feature.history.domain.model

import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

/**
 * Domain model representing a history item.
 * Maps from SyncTask to provide history view.
 */
@OptIn(ExperimentalTime::class)
data class HistoryItem(
    val id: String,
    val type: HistoryType,
    val entityId: String,      // Order ID, Invoice ID, etc.
    val status: HistoryStatus,
    val timestamp: Instant,
    val attempts: Int,
    val lastError: String?,
    val priority: Int
)
