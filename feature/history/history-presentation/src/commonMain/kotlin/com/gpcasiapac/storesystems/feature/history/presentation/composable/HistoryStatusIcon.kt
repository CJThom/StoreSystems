package com.gpcasiapac.storesystems.feature.history.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NearbyError
import androidx.compose.material.icons.outlined.SystemSecurityUpdateWarning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

@Composable
fun HistoryStatusIcon(
    status: HistoryStatus,
    modifier: Modifier = Modifier,
    isOutline: Boolean = false
) {
    val backgroundColor = when (status) {
        HistoryStatus.PENDING -> MaterialTheme.colorScheme.tertiary
        HistoryStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
        HistoryStatus.COMPLETED -> Color(0XFF097969)//MaterialTheme.extendedColorScheme.success //(06/11/25) Success uses territory need to update?.
        HistoryStatus.FAILED -> MaterialTheme.colorScheme.onErrorContainer
        HistoryStatus.RETRYING -> MaterialTheme.colorScheme.secondary
        HistoryStatus.REQUIRES_ACTION -> MaterialTheme.colorScheme.error
    }

    val icon = when (status) {
        HistoryStatus.PENDING -> Icons.Default.HourglassEmpty
        HistoryStatus.IN_PROGRESS -> Icons.Default.Refresh
        HistoryStatus.COMPLETED -> Icons.Default.Check
        HistoryStatus.FAILED -> Icons.Default.Close
        HistoryStatus.RETRYING -> Icons.Default.Refresh
        HistoryStatus.REQUIRES_ACTION -> Icons.Outlined.Error

    }
    Box(
        modifier = modifier
            .size(Dimens.Size.iconMedium)
            .let {
                if (isOutline) it.border(Dimens.Stroke.normal, backgroundColor, CircleShape)
                else it.background(backgroundColor, CircleShape)
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = status.name,
            tint = if(isOutline) backgroundColor else MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(Dimens.Size.iconSmall)
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun HistoryStatusIconColoredPreviewAll() {
    GPCTheme {
        Surface {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HistoryStatusIcon(status = HistoryStatus.PENDING, isOutline = true)
                HistoryStatusIcon(status = HistoryStatus.IN_PROGRESS, isOutline = true)
                HistoryStatusIcon(status = HistoryStatus.COMPLETED, isOutline = true)
                HistoryStatusIcon(status = HistoryStatus.FAILED, isOutline = true)
                HistoryStatusIcon(status = HistoryStatus.RETRYING, isOutline = true)
                HistoryStatusIcon(status = HistoryStatus.REQUIRES_ACTION, isOutline = true)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun HistoryStatusIconPreviewAll() {
    GPCTheme {
        Surface {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HistoryStatusIcon(status = HistoryStatus.PENDING)
                HistoryStatusIcon(status = HistoryStatus.IN_PROGRESS)
                HistoryStatusIcon(status = HistoryStatus.COMPLETED)
                HistoryStatusIcon(status = HistoryStatus.FAILED)
                HistoryStatusIcon(status = HistoryStatus.RETRYING)
                HistoryStatusIcon(status = HistoryStatus.REQUIRES_ACTION)
            }
        }
    }
}
