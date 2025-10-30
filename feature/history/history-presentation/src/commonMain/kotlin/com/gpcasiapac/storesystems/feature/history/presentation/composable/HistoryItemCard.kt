package com.gpcasiapac.storesystems.feature.history.presentation.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import com.gpcasiapac.storesystems.feature.history.presentation.mapper.toCustomerTypeParam
import com.gpcasiapac.storesystems.foundation.component.CollectOrderDetailsContent
import com.gpcasiapac.storesystems.foundation.component.ListItemScaffold
import com.gpcasiapac.storesystems.foundation.component.ListItemToolbarScaffold
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Main composable for rendering a history item card.
 * Delegates to specific composables based on HistoryItem subtype.
 */
@Composable
fun HistoryItemCard(
    item: HistoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (item) {
        is CollectHistoryItem -> CollectHistoryItemCard(
            item = item,
            onClick = onClick,
            modifier = modifier
        )
    }
}

/**
 * Composable for history items with collect metadata.
 * Shows order and customer information.
 */
@Composable
private fun CollectHistoryItemCard(
    item: CollectHistoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val metadata = item.metadata.maxByOrNull { it.orderPickedAt } ?: item.metadata.firstOrNull()
    if (metadata == null) return

    ListItemScaffold(
        modifier = modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(),
        toolbar = {
//            CollectOrderHistoryToolbar(
//                status = item.status,
//                actions = {
//
//                }
//            )
        }
    ) {
        CollectOrderDetailsContent(
            customerName = metadata.getCustomerDisplayName(),
            customerType = metadata.customerType.toCustomerTypeParam(),
            invoiceNumber = metadata.invoiceNumber,
            webOrderNumber = metadata.webOrderNumber,
            isLoading = false,
            contentPadding = PaddingValues(bottom = Dimens.Space.small)
        )
    }
}

@Composable
private fun RowScope.CollectOrderHistoryToolbar(
    status: HistoryStatus,
    actions: @Composable RowScope.() -> Unit,
) {
    ListItemToolbarScaffold(
        actions = actions,
        overflowMenu = { dismiss ->

        }
    ) {
//        Row {
//            StatusBadge(status = status)
//        }
    }
}


/**
 * Composable for history items without metadata.
 * Shows basic task information.
 */
@Composable
private fun GenericHistoryItem(
    item: HistoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = {
            Text(
                text = "Task #${item.entityId}",
                style = MaterialTheme.typography.titleMedium
            )
        },
        supportingContent = {
            Text(
                text = formatTimeAgo(item.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            StatusBadge(status = item.status)
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    )
}

/**
 * Status badge composable.
 */
@Composable
fun StatusBadge(
    status: HistoryStatus,
    modifier: Modifier = Modifier
) {
    val (text, containerColor, contentColor) = when (status) {
        HistoryStatus.PENDING, HistoryStatus.RETRYING -> Triple(
            "Pending",
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        )
        HistoryStatus.IN_PROGRESS -> Triple(
            "In Progress",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
        HistoryStatus.COMPLETED -> Triple(
            "Completed",
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        HistoryStatus.FAILED, HistoryStatus.REQUIRES_ACTION -> Triple(
            "Failed",
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
    }
    
    Surface(
        shape = MaterialTheme.shapes.small,
        color = containerColor,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Format timestamp to relative time string.
 */
@OptIn(ExperimentalTime::class)
fun formatTimeAgo(timestamp: kotlinx.datetime.Instant): String {
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
