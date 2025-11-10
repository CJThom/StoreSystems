package com.gpcasiapac.storesystems.feature.history.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Sealed history item hierarchy. Each subtype represents a concrete history kind
 * and may carry strongly-typed metadata specific to that kind.
 */
@OptIn(ExperimentalTime::class)
sealed interface HistoryItem {
    val id: String
    val entityId: String
    val status: HistoryStatus
    val timestamp: Instant
    val attempts: Int
    val lastError: String?
    val priority: Int
}

/**
 * Collect-only history item. Enforces list of CollectMetadata.
 */
@OptIn(ExperimentalTime::class)
 data class CollectHistoryItem(
    override val id: String,
    override val entityId: String,
    override val status: HistoryStatus,
    override val timestamp: Instant,
    override val attempts: Int,
    override val lastError: String?,
    override val priority: Int,
    val submittedBy: String?,
    val requestId: String,
    val metadata: List<HistoryMetadata.CollectMetadata>
) : HistoryItem
