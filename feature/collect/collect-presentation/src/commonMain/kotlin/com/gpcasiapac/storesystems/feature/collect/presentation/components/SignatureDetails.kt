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
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SignatureDetails(
    name: String?,
    dateTime: String?,
    onRetakeClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = Dimens.Space.medium,
        vertical = Dimens.Space.small
    )
) {
    Column(
        modifier = modifier.padding(contentPadding),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.small)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
        ) {
            // Left side: name and date/time
            Text(
                text = if (!name.isNullOrBlank() && !dateTime.isNullOrBlank()) {
                    "$name  •  $dateTime"
                } else {
                    ""
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }
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
    val dateTime: String?
)

private class SignatureDetailsPreviewParameterProvider :
    PreviewParameterProvider<SignatureDetailsPreviewData> {
    override val values: Sequence<SignatureDetailsPreviewData> = sequenceOf(
        // Both present (various name lengths and character sets)
        SignatureDetailsPreviewData(name = "JD", dateTime = "4 Nov 2025, 6:36 PM"),
        SignatureDetailsPreviewData(name = "John Doe", dateTime = "4 Nov 2025, 6:36 PM"),
        SignatureDetailsPreviewData(name = "Alexandra Johnson", dateTime = "4 Nov 2025, 6:36 PM"),
        SignatureDetailsPreviewData(name = "Maximilian Alexander von Habsburg", dateTime = "4 Nov 2025, 6:36 PM"),
        SignatureDetailsPreviewData(name = "A very very very very long customer name that should wrap correctly in the row", dateTime = "4 Nov 2025, 6:36 PM"),
        SignatureDetailsPreviewData(name = "O’Connor", dateTime = "4 Nov 2025, 6:36 PM"),
        SignatureDetailsPreviewData(name = "Anne-Marie", dateTime = "4 Nov 2025, 6:36 PM"),
        SignatureDetailsPreviewData(name = "李小龍 (Bruce Lee)", dateTime = "4 Nov 2025, 6:36 PM"),
        SignatureDetailsPreviewData(name = "山田 太郎", dateTime = "4 Nov 2025, 6:36 PM"),
        SignatureDetailsPreviewData(name = "Алексей Петров", dateTime = "4 Nov 2025, 6:36 PM"),
        SignatureDetailsPreviewData(name = "أحمد محمد", dateTime = "4 Nov 2025, 6:36 PM"),
        SignatureDetailsPreviewData(name = "José Ángel", dateTime = "4 Nov 2025, 6:36 PM"),
        SignatureDetailsPreviewData(name = "Zoë Kravitz", dateTime = "4 Nov 2025, 6:36 PM"),
        SignatureDetailsPreviewData(name = "Renée Zellweger", dateTime = "4 Nov 2025, 6:36 PM"),
        SignatureDetailsPreviewData(name = "Søren Kierkegaard", dateTime = "4 Nov 2025, 6:36 PM"),
        SignatureDetailsPreviewData(name = "Miyazaki はじめ", dateTime = "4 Nov 2025, 6:36 PM"),
        SignatureDetailsPreviewData(name = "User 😊✨", dateTime = "4 Nov 2025, 6:36 PM"),
        SignatureDetailsPreviewData(name = "Name-With—Em—Dash", dateTime = "4 Nov 2025, 6:36 PM"),
        SignatureDetailsPreviewData(name = "Tabbed\tName", dateTime = "4 Nov 2025, 6:36 PM"),
        // Longer date string variants
        SignatureDetailsPreviewData(name = "Long Date Example", dateTime = "4 November 2025, 6:36 PM"),
        SignatureDetailsPreviewData(name = "24h Time", dateTime = "4 Nov 2025, 18:36"),
        SignatureDetailsPreviewData(name = "With Seconds", dateTime = "4 Nov 2025, 6:36:59 PM"),

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
