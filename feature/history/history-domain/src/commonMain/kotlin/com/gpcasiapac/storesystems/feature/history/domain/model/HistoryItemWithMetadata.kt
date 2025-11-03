package com.gpcasiapac.storesystems.feature.history.domain.model

import com.gpcasiapac.storesystems.feature.history.api.HistoryType
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

/**
 * Unified domain model for history items with typed metadata.
 * This model combines base history information with zero or more typed metadata entries.
 *
 * Uses sealed interface for metadata to enable type-safe handling in UI layer.
 */
@OptIn(ExperimentalTime::class)
 data class HistoryItemWithMetadata(
    // Base history fields
   val id: String,
   val type: HistoryType,
   val entityId: String,
   val status: HistoryStatus,
   val timestamp: Instant,
   val attempts: Int,
   val lastError: String?,
   val priority: Int,
    
    // Typed metadata entries (empty when none)
   val metadataList: List<HistoryMetadata>
)
