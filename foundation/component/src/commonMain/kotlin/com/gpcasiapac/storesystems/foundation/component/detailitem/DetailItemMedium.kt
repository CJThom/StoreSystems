package com.gpcasiapac.storesystems.foundation.component.detailitem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

/**
 * A reusable component for displaying detail items with an icon, label, and value.
 * Used for customer information display in various screens.
 */
@Composable
fun DetailItemMedium(
    imageVector: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false, // Todo add isLoading
    contentPadding: PaddingValues = PaddingValues()
) {
    Row(
        modifier = modifier
            .padding(contentPadding)
            .width(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
    ) {

        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier.size(Dimens.Size.iconMedium),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private data class DetailItemMediumPreviewData(
    val icon: ImageVector,
    val label: String,
    val value: String
)

private class DetailItemMediumPreviewProvider : PreviewParameterProvider<DetailItemMediumPreviewData> {
    override val values = sequenceOf(
        DetailItemMediumPreviewData(
            icon = Icons.Outlined.Person,
            label = "Customer Number",
            value = "1887388193"
        ),
        DetailItemMediumPreviewData(
            icon = Icons.Outlined.Phone,
            label = "Phone",
            value = "0455 100 000"
        ),
        DetailItemMediumPreviewData(
            icon = Icons.Outlined.Email,
            label = "Email",
            value = "example@example.com"
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun DetailItemMediumPreview(
    @PreviewParameter(DetailItemMediumPreviewProvider::class) data: DetailItemMediumPreviewData
) {
    GPCTheme {
        DetailItemMedium(
            imageVector = data.icon,
            label = data.label,
            value = data.value,
            contentPadding = PaddingValues(Dimens.Space.medium)
        )
    }
}
