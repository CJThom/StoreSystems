package com.gpcasiapac.storesystems.feature.history.presentation.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.kotlin.extension.toTimeAgoString
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HistoryHeader(
    modifier: Modifier = Modifier,
    username: String,
    time: Instant?,
    status: HistoryStatus,
    enabledRetry: Boolean = true,
    onRetryClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Crossfade(
                targetState = status,
                label = "HistoryStatusCrossfade"
            ) { currentStatus ->
                if (currentStatus == HistoryStatus.IN_PROGRESS || currentStatus == HistoryStatus.RETRYING) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(Dimens.Size.iconMedium),
                        strokeWidth = Dimens.Stroke.normal,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    HistoryStatusIcon(status = currentStatus)
                }
            }
            Spacer(modifier = Modifier.width(Dimens.Space.small))

            Column {
                Text(
                    text = username,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = time?.toTimeAgoString() ?: "",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        AnimatedVisibility(
            visible = status == HistoryStatus.FAILED || status == HistoryStatus.REQUIRES_ACTION,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            OutlinedIconButton(
                enabled = enabledRetry,
                colors = IconButtonDefaults.outlinedIconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContentColor = Color.Gray
                ),
                border = BorderStroke(
                    Dimens.Stroke.normal,
                    if (enabledRetry) MaterialTheme.colorScheme.onSurfaceVariant else Color.Gray
                ),
                shape = CircleShape,
                onClick = onRetryClick
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry",
                    modifier = Modifier.size(Dimens.Size.iconSmall)
                )
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun HistoryHeaderPreviewRetrying() {
    GPCTheme {
        HistoryHeader(
            modifier = Modifier.padding(
                horizontal = Dimens.Space.medium,
                vertical = Dimens.Space.small
            ),
            username = "Username",
            time = Instant.fromEpochMilliseconds(1695737600000),
            onRetryClick = {},
            enabledRetry = false,
            status = HistoryStatus.COMPLETED
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryHeaderPreviewDisabledRetry() {
    GPCTheme {
        HistoryHeader(
            modifier = Modifier.padding(
                horizontal = Dimens.Space.medium,
                vertical = Dimens.Space.small
            ),
            username = "Username",
            time = Instant.fromEpochMilliseconds(1695737600000),
            onRetryClick = {},
            enabledRetry = false,
            status = HistoryStatus.COMPLETED
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HistoryHeaderLoadingPreview() {
    GPCTheme {
        HistoryHeader(
            modifier = Modifier.padding(
                horizontal = Dimens.Space.medium,
                vertical = Dimens.Space.small
            ),
            username = "Username",
            time = Instant.fromEpochMilliseconds(1695737600000),
            onRetryClick = {},
            enabledRetry = true,
            status = HistoryStatus.IN_PROGRESS
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryHeaderPreview() {
    GPCTheme {
        HistoryHeader(
            modifier = Modifier.padding(
                horizontal = Dimens.Space.medium,
                vertical = Dimens.Space.small
            ),
            username = "Username",
            time = Clock.System.now(),
            onRetryClick = {},
            enabledRetry = true,
            status = HistoryStatus.PENDING
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryHeaderRetryingPreview() {
    GPCTheme {
        HistoryHeader(
            modifier = Modifier.padding(
                horizontal = Dimens.Space.medium,
                vertical = Dimens.Space.small
            ),
            username = "Username",
            time = Clock.System.now(),
            onRetryClick = {},
            enabledRetry = true,
            status = HistoryStatus.REQUIRES_ACTION
        )
    }
}