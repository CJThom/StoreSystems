package com.gpcasiapac.storesystems.feature.history.presentation.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItemWithMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import com.gpcasiapac.storesystems.foundation.component.ListItemScaffold
import com.gpcasiapac.storesystems.foundation.component.ListItemToolbarScaffold
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Main composable for rendering a history item card.
 * Delegates to specific composables based on metadata type.
 */
@Composable
fun HistoryItemCard(
    item: HistoryItemWithMetadata,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Render different UI based on metadata type (sealed class)
    when (val metadata = item.metadata) {
        is HistoryMetadata.CollectMetadata -> {
            CollectHistoryItem(
                item = item,
                metadata = metadata,
                onClick = onClick,
                modifier = modifier
            )
        }
        is HistoryMetadata.NoMetadata -> {
            GenericHistoryItem(
                item = item,
                onClick = onClick,
                modifier = modifier
            )
        }
        // Future metadata types can be added here:
        // is HistoryMetadata.PaymentMetadata -> PaymentHistoryItem(...)
    }
}

/**
 * Composable for history items with collect metadata.
 * Shows order and customer information.
 */
@Composable
private fun CollectHistoryItem(
    item: HistoryItemWithMetadata,
    metadata: HistoryMetadata.CollectMetadata,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItemScaffold(
        modifier = modifier,
        contentPadding = contendPadding,
        toolbar = {
            CollectOrderHistoryToolbar(
                actions = {

                }
            )
        }
    ) {
        CollectOrderDetailsContent(
            customerName = customerName,
            customerType = customerType,
            invoiceNumber = invoiceNumber,
            webOrderNumber = webOrderNumber,
            isLoading = isLoading,
            contentPadding = PaddingValues(bottom = Dimens.Space.small)
        )
    }
    ListItem(
        headlineContent = {
            Text(
                text = "Order #${metadata.invoiceNumber}",
                style = MaterialTheme.typography.titleMedium
            )
        },
        supportingContent = {
            Column {
                Text(
                    text = "Customer: ${metadata.getCustomerDisplayName()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatTimeAgo(item.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (item.attempts > 0 && item.status == HistoryStatus.FAILED) {
                    Text(
                        text = "${item.attempts} attempt${if (item.attempts > 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
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

@Composable
private fun RowScope.CollectOrderHistoryToolbar(
    actions: @Composable RowScope.() -> Unit,
) {
    ListItemToolbarScaffold(
        actions = actions,
        overflowMenu = { dismiss ->
            DropdownMenuItem(
                text = { Text("Select all by customer") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.SelectAll,
                        contentDescription = "Select all"
                    )
                },
                onClick = { dismiss() }
            )
        }
    ) {
        Text("Some Value")
    }
}


/**
 * Composable for history items without metadata.
 * Shows basic task information.
 */
@Composable
private fun GenericHistoryItem(
    item: HistoryItemWithMetadata,
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
            Column {
                Text(
                    text = "Type: ${item.type.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatTimeAgo(item.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
private fun StatusBadge(
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
