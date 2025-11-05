package com.gpcasiapac.storesystems.feature.history.presentation.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
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
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.foundation.placeholder
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HistoryHeader(
    modifier: Modifier = Modifier,
    username: String,
    timeAgo: Instant,
    status: HistoryStatus,
    enabledRetry: Boolean = true,
    isRetrying: Boolean = false,
    onRetryClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            HistoryStatusIcon(
                status = status
            )
            Spacer(modifier = Modifier.width(Dimens.Space.small))

            Column {
                Text(
                    text = username,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = timeAgo.toString(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        if(isRetrying){
            CircularProgressIndicator(
                modifier = Modifier
                    .size(Dimens.Size.iconExtraLarge)
                    .padding(Dimens.Space.medium),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }else{
            OutlinedIconButton(
                enabled = enabledRetry,
                colors = IconButtonDefaults.outlinedIconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContentColor = Color.Gray
                ),
                border = BorderStroke(
                    1.dp,
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
            timeAgo = Instant.fromEpochMilliseconds(1695737600000),
            onRetryClick = {},
            isRetrying = true,
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
            timeAgo = Instant.fromEpochMilliseconds(1695737600000),
            onRetryClick = {},
            enabledRetry = false,
            status = HistoryStatus.COMPLETED
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
            timeAgo = Instant.fromEpochMilliseconds(1695737600000),
            onRetryClick = {},
            enabledRetry = true,
            status = HistoryStatus.PENDING
        )
    }
}