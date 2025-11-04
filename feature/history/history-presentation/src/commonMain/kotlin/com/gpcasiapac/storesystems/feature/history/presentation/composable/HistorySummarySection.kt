package com.gpcasiapac.storesystems.feature.history.presentation.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem
import com.gpcasiapac.storesystems.foundation.design_system.Dimens

@Composable
fun SummarySection(
    resolved: CollectHistoryItem
) {
    val meta = resolved.metadata.firstOrNull()
    val submittedAtText = meta?.orderCreatedAt?.let { formatTimeAgo(it) } ?: "-"
    val submittedByText = meta?.getCustomerDisplayName() ?: "-"
    val itemCount = resolved.metadata.size

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = "Status",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        StatusBadge(status = resolved.status)
    }

    Spacer(Modifier.height(Dimens.Space.small))

    KeyValueRow(label = "Submitted on", value = submittedAtText)
    KeyValueRow(label = "Submitted by", value = submittedByText)
    KeyValueRow(label = "Items", value = itemCount.toString())
    KeyValueRow(label = "Attempts", value = resolved.attempts.toString())

    if (!resolved.lastError.isNullOrBlank()) {
        Spacer(Modifier.height(Dimens.Space.small))
        ErrorInfo(message = resolved.lastError!!)
    }
}

@Composable
fun KeyValueRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    singleLine: Boolean = false,
    maxValueLines: Int = 3,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            modifier = Modifier.weight(1.5f),
            maxLines = if (singleLine) 1 else maxValueLines,
            overflow = TextOverflow.Ellipsis,
            softWrap = true,
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun ErrorInfo(message: String) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Column(modifier = Modifier.padding(Dimens.Space.small)) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun InfoPanel(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    showBorder: Boolean = false,
    content: @Composable () -> Unit,
) {
    val border = if (showBorder) {
        BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    } else null

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 1.dp,
        shadowElevation = 0.dp,
        shape = MaterialTheme.shapes.medium,
        border = border
    ) {
        Column(Modifier.padding(contentPadding)) { content() }
    }
}
