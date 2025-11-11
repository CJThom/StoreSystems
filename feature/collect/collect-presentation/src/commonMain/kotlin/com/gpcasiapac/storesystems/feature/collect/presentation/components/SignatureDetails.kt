package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ModeEdit
import androidx.compose.material.icons.outlined.RestoreFromTrash
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.kotlin.extension.toLocalDateLongString
import com.gpcasiapac.storesystems.common.kotlin.extension.toLocalDateTimeLongString
import com.gpcasiapac.storesystems.common.kotlin.extension.toLocalDateTimeMediumString
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SignatureDetails(
    name: String?,
    dateTime: Instant?,
    onRetakeClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = Dimens.Space.medium,
        vertical = Dimens.Space.small
    )
) {
    Row(
        modifier = modifier
            .padding(contentPadding)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
    ) {
        // Left side: Column with name and date/time
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.Space.small)
        ) {
            if (!name.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Signed by:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.size(Dimens.Space.extraSmall))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
            if (dateTime != null) {
                Text(
                    text = dateTime.toLocalDateTimeMediumString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
        // Right side: Retake button
        OutlinedButton(
            onClick = onRetakeClick,
            modifier = Modifier.height(ButtonDefaults.ExtraSmallContainerHeight),
            contentPadding = ButtonDefaults.ExtraSmallContentPadding,
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Retake",
                modifier = Modifier.size(ButtonDefaults.iconSizeFor(ButtonDefaults.ExtraSmallIconSize)),
            )
            Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(ButtonDefaults.ExtraSmallIconSize)))
            Text("RETAKE")
        }
    }
}

// ------ Previews with PreviewParameter ------
private data class SignatureDetailsPreviewData(
    val name: String?,
    val dateTime: Instant?
)

private class SignatureDetailsPreviewParameterProvider :
    PreviewParameterProvider<SignatureDetailsPreviewData> {
    override val values: Sequence<SignatureDetailsPreviewData> = sequenceOf(
        // Both present (various name lengths and character sets)
        SignatureDetailsPreviewData(
            name = "JD",
            dateTime = Instant.fromEpochMilliseconds(1730332560000)
        ), // 4 Nov 2025, 6:36 PM
        SignatureDetailsPreviewData(
            name = "John Doe",
            dateTime = Instant.fromEpochMilliseconds(1730332560000)
        ),
        SignatureDetailsPreviewData(
            name = "Alexandra Johnson",
            dateTime = Instant.fromEpochMilliseconds(1730332560000)
        ),
        SignatureDetailsPreviewData(
            name = "Maximilian Alexander von Habsburg",
            dateTime = Instant.fromEpochMilliseconds(1692505600000)
        ),
        SignatureDetailsPreviewData(
            name = "A very very very very long customer name that should wrap correctly in the row",
            dateTime = Instant.fromEpochMilliseconds(1692505600000)
        ),
        SignatureDetailsPreviewData(
            name = "O'Connor",
            dateTime = Instant.fromEpochMilliseconds(1730332560000)
        ),
        SignatureDetailsPreviewData(
            name = "Anne-Marie",
            dateTime = Instant.fromEpochMilliseconds(1730332560000)
        ),
        SignatureDetailsPreviewData(
            name = "李小龍 (Bruce Lee)",
            dateTime = Instant.fromEpochMilliseconds(1730332560000)
        ),
        SignatureDetailsPreviewData(
            name = "山田 太郎",
            dateTime = Instant.fromEpochMilliseconds(1730332560000)
        ),
        SignatureDetailsPreviewData(
            name = "Алексей Петров",
            dateTime = Instant.fromEpochMilliseconds(1730332560000)
        ),
        SignatureDetailsPreviewData(
            name = "أحمد محمد",
            dateTime = Instant.fromEpochMilliseconds(1730332560000)
        ),
        SignatureDetailsPreviewData(
            name = "José Ángel",
            dateTime = Instant.fromEpochMilliseconds(1730332560000)
        ),
        SignatureDetailsPreviewData(
            name = "Zoë Kravitz",
            dateTime = Instant.fromEpochMilliseconds(1730332560000)
        ),
        SignatureDetailsPreviewData(
            name = "Renée Zellweger",
            dateTime = Instant.fromEpochMilliseconds(1730332560000)
        ),
        SignatureDetailsPreviewData(
            name = "Søren Kierkegaard",
            dateTime = Instant.fromEpochMilliseconds(1730332560000)
        ),
        SignatureDetailsPreviewData(
            name = "Miyazaki はじめ",
            dateTime = Instant.fromEpochMilliseconds(1730332560000)
        ),
        SignatureDetailsPreviewData(
            name = "User 😊✨",
            dateTime = Instant.fromEpochMilliseconds(1730332560000)
        ),
        SignatureDetailsPreviewData(
            name = "Name-With—Em—Dash",
            dateTime = Instant.fromEpochMilliseconds(1730332560000)
        ),
        SignatureDetailsPreviewData(
            name = "Tabbed\tName",
            dateTime = Instant.fromEpochMilliseconds(1730332560000)
        ),
        // Longer date string variants
        SignatureDetailsPreviewData(
            name = "Long Date Example",
            dateTime = Instant.fromEpochMilliseconds(1692505600000)
        ),
        SignatureDetailsPreviewData(name = "24h Time",    dateTime = Instant.fromEpochMilliseconds(1692505600000)),
        SignatureDetailsPreviewData(name = "With Seconds",    dateTime = Instant.fromEpochMilliseconds(1692505600000)),

        // Both null (nothing shown on the left text)
        SignatureDetailsPreviewData(name = null, dateTime = null),
    )
}

@Preview(showBackground = true)
@Composable
private fun SignatureDetailsPreview(
    @PreviewParameter(SignatureDetailsPreviewParameterProvider::class)
    data: SignatureDetailsPreviewData
) {
    GPCTheme {
        SignatureDetails(
            name = data.name,
            dateTime = data.dateTime,
            onRetakeClick = {},
        )
    }
}
