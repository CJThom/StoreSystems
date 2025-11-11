package com.gpcasiapac.storesystems.feature.history.presentation.model

import com.gpcasiapac.storesystems.feature.history.api.HistoryType
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import kotlin.time.Instant


/**
 * Lightweight UI model for a single history row.
 */
 data class HistoryListItemState(
   val id: String,
   val type: HistoryType,
   val customerName: String,
   val invoiceNumbers: List<String>,
   val status: HistoryStatus,
   val submittedAt: Instant?,
   val canRetry: Boolean,
   val submittedBy: String = "",
)