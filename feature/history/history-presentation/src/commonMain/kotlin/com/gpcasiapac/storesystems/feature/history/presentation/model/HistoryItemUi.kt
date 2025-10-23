package com.gpcasiapac.storesystems.feature.history.presentation.model

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus

/**
 * UI model for history item display.
 */
@Immutable
data class HistoryItemUi(
    val id: String,
    val title: String,           // "Order #12345"
    val subtitle: String,        // "Submitted 2 hours ago"
    val status: HistoryStatus,
    val statusText: String,      // "Pending", "Failed", "Completed"
    val statusColor: HistoryStatusColor,
    val attempts: String?,       // "3 attempts" or null
    val showRetry: Boolean       // Show retry button for failed items
)

enum class HistoryStatusColor {
    PENDING,    // Yellow/Orange
    SUCCESS,    // Green
    ERROR,      // Red
    INFO        // Blue
}
