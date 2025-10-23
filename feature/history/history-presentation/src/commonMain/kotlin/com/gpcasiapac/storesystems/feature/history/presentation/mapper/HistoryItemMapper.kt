package com.gpcasiapac.storesystems.feature.history.presentation.mapper

import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryType
import com.gpcasiapac.storesystems.feature.history.presentation.model.HistoryItemUi
import com.gpcasiapac.storesystems.feature.history.presentation.model.HistoryStatusColor
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Extension function to map HistoryItem to HistoryItemUi.
 */
@OptIn(ExperimentalTime::class)
fun HistoryItem.toUi(): HistoryItemUi {
    return HistoryItemUi(
        id = id,
        title = formatTitle(),
        subtitle = formatSubtitle(),
        status = status,
        statusText = formatStatus(),
        statusColor = mapStatusColor(),
        attempts = if (attempts > 0 && status == HistoryStatus.FAILED) {
            "$attempts attempt${if (attempts > 1) "s" else ""}"
        } else null,
        showRetry = status == HistoryStatus.FAILED
    )
}

private fun HistoryItem.formatTitle(): String {
    return when (type) {
        HistoryType.ORDER_SUBMISSION -> "Order #$entityId"
        HistoryType.UNKNOWN -> "Task #$entityId"
    }
}

private fun HistoryItem.formatSubtitle(): String {
    val timeAgo = formatTimeAgo(timestamp)
    val action = when (type) {
        HistoryType.ORDER_SUBMISSION -> "Submitted"
        HistoryType.UNKNOWN -> "Created"
    }
    return "$action $timeAgo"
}

private fun HistoryItem.formatStatus(): String {
    return when (status) {
        HistoryStatus.PENDING -> "Pending"
        HistoryStatus.IN_PROGRESS -> "In Progress"
        HistoryStatus.COMPLETED -> "Completed"
        HistoryStatus.FAILED -> "Failed"
        HistoryStatus.RETRYING -> "Retrying"
        HistoryStatus.REQUIRES_ACTION -> "Requires Action"
    }
}

private fun HistoryItem.mapStatusColor(): HistoryStatusColor {
    return when (status) {
        HistoryStatus.PENDING, HistoryStatus.RETRYING -> HistoryStatusColor.PENDING
        HistoryStatus.IN_PROGRESS -> HistoryStatusColor.INFO
        HistoryStatus.COMPLETED -> HistoryStatusColor.SUCCESS
        HistoryStatus.FAILED, HistoryStatus.REQUIRES_ACTION -> HistoryStatusColor.ERROR
    }
}

@OptIn(ExperimentalTime::class)
private fun formatTimeAgo(timestamp: kotlinx.datetime.Instant): String {
    // Convert kotlinx.datetime.Instant to kotlin.time.Instant
    val ktInstant = Instant.fromEpochMilliseconds(timestamp.toEpochMilliseconds())
    val now = Clock.System.now()
    val duration = now - ktInstant
    
    return when {
        duration < 1.minutes -> "just now"
        duration < 1.hours -> {
            val mins = duration.inWholeMinutes
            "$mins minute${if (mins > 1) "s" else ""} ago"
        }
        duration < 24.hours -> {
            val hrs = duration.inWholeHours
            "$hrs hour${if (hrs > 1) "s" else ""} ago"
        }
        duration < 7.days -> {
            val dys = duration.inWholeDays
            "$dys day${if (dys > 1) "s" else ""} ago"
        }
        else -> {
            val weeks = duration.inWholeDays / 7
            "$weeks week${if (weeks > 1) "s" else ""} ago"
        }
    }
}

/**
 * Extension function to map list of HistoryItems to HistoryItemUi.
 */
fun List<HistoryItem>.toUi(): List<HistoryItemUi> {
    return map { it.toUi() }
}
