package com.gpcasiapac.storesystems.feature.history.presentation.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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

    Spacer(Modifier.height(Dimens.Space.medium))

    KeyValueRow(label = "Submitted on", value = submittedAtText)
    Spacer(Modifier.height(Dimens.Space.small))
    KeyValueRow(label = "Submitted by", value = submittedByText)
//    KeyValueRow(label = "Items", value = itemCount.toString())
//    KeyValueRow(label = "Attempts", value = resolved.attempts.toString())
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
fun ErrorInfo(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium),
    message: String,
    attempts: Int,
    shape: Shape = RoundedCornerShape(8.dp),
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.error,
                shape = shape
            ),
        color = MaterialTheme.colorScheme.errorContainer,
        shape = shape,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .padding(contentPadding),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier
                    .size(Dimens.Size.iconSmall)
                    .padding(Dimens.Space.small),
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                maxLines = 4,
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = attempts.toString(),
                modifier = Modifier.padding(Dimens.Space.small),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
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
