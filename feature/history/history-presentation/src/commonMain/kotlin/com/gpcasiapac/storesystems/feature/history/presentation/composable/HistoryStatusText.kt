package com.gpcasiapac.storesystems.feature.history.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

@Composable
fun HistoryStatusText(
    status: HistoryStatus,
    modifier: Modifier = Modifier
) {
    // Match color mapping from HistoryStatusIcon
    val backgroundColor = when (status) {
        HistoryStatus.PENDING -> MaterialTheme.colorScheme.tertiary
        HistoryStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
        HistoryStatus.COMPLETED -> Color(0xFF097969) // same success color
        HistoryStatus.FAILED -> MaterialTheme.colorScheme.onErrorContainer
        HistoryStatus.RETRYING -> MaterialTheme.colorScheme.secondary
        HistoryStatus.REQUIRES_ACTION -> MaterialTheme.colorScheme.error
    }
    val text = when (status) {
        HistoryStatus.PENDING -> "Pending"
        HistoryStatus.IN_PROGRESS -> "In Progress"
        HistoryStatus.COMPLETED -> "Completed"
        HistoryStatus.FAILED -> "Failed"
        HistoryStatus.RETRYING -> "Retrying"
        HistoryStatus.REQUIRES_ACTION -> "Action Needed"
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        HistoryStatusIcon(
            status = status
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = backgroundColor,
            modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun StatusBadgePreviewHorizontal() {
    GPCTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            HistoryStatus.entries.forEach { status ->
                HistoryStatusText(status = status)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusBadgePreviewVertical() {
    GPCTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            HistoryStatus.entries.forEach { status ->
                HistoryStatusText(status = status)
            }
        }
    }
}
