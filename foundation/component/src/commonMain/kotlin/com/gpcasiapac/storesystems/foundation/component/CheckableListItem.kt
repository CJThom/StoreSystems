package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

/**
 * Reusable M3 ListItem with a leading Checkbox where the entire row is clickable.
 * - Only the row click toggles the state to avoid double events; the Checkbox is display-only.
 * - Exposes contentPadding so callers can align it with surrounding content.
 */
@Composable
fun CheckableListItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    subtitle: String? = null,
    contentPadding: PaddingValues = PaddingValues(),
) {
    ListItem(
        modifier = modifier
            .padding(contentPadding)
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                role = Role.Checkbox,
                onClick = { onCheckedChange(!checked) }
            ),
        leadingContent = {
            Checkbox(
                checked = checked,
                onCheckedChange = null, // Row handles the toggle to make whole row clickable
                enabled = enabled,
            )
        },
        headlineContent = {
            Text(title)
        },
        supportingContent = if (!subtitle.isNullOrBlank()) {
            {
                Text(subtitle)
            }
        } else {
            null
        },
    )
}

// ----- Previews -----
private data class CheckableListItemPreviewData(
    val title: String,
    val checked: Boolean,
    val enabled: Boolean,
    val subtitle: String? = null,
    val contentPadding: PaddingValues = PaddingValues()
)

private class CheckableListItemPreviewProvider :
    PreviewParameterProvider<CheckableListItemPreviewData> {
    override val values: Sequence<CheckableListItemPreviewData> = sequenceOf(
        CheckableListItemPreviewData(
            title = "ID Verified",
            checked = true,
            enabled = true,
            subtitle = null,
        ),
        CheckableListItemPreviewData(
            title = "Email customer",
            checked = false,
            enabled = true,
            subtitle = "We will send a confirmation email",
        ),
        CheckableListItemPreviewData(
            title = "Requires manager approval",
            checked = false,
            enabled = false,
            subtitle = "You do not have permission",
        ),
    )
}

@Preview(name = "CheckableListItem - Variants")
@Composable
private fun CheckableListItemPreview(
    @PreviewParameter(CheckableListItemPreviewProvider::class) data: CheckableListItemPreviewData
) {
    GPCTheme {
        Surface {
            CheckableListItem(
                title = data.title,
                checked = data.checked,
                enabled = data.enabled,
                subtitle = data.subtitle,
                contentPadding = data.contentPadding,
                onCheckedChange = { /* Preview: no-op */ }
            )
        }
    }
}