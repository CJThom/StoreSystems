package com.gpcasiapac.storesystems.foundation.component.detailitem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BackHand
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.foundation.PlaceholderDefaults
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.color
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

@Composable
fun DetailItemSmall(
    value: String,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.extraSmall)
) {
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
        DetailItemContent(
            value = value,
            imageVector = imageVector,
            modifier = modifier,
            isLoading = isLoading,
            contentPadding = contentPadding
        )
    }
}

@Composable
fun DetailItemSmallChip(
    value: String,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    // onClick: (() -> Unit)? = null, // todo onClick adds hit padding
    //  enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.extraSmall)
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        modifier = modifier.placeholder(
            visible = isLoading,
            color = PlaceholderDefaults.color(backgroundColor = MaterialTheme.colorScheme.tertiaryContainer)
        ),
//        onClick = onClick ?: {},
//        enabled = enabled,
    ) {
        DetailItemContent(
            value = value,
            imageVector = imageVector,
            modifier = Modifier,
            isLoading = isLoading,
            contentPadding = contentPadding
        )
    }
}

@Composable
private fun DetailItemContent(
    value: String,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.extraSmall)
) {
    Row(
        modifier = modifier
            .padding(contentPadding)
            .padding(end = Dimens.Space.extraSmall)
            .placeholder(visible = isLoading),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier.size(Dimens.Size.iconSmall)

        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            maxLines = 1,
            modifier = Modifier,
        )

    }
}

private data class DetailItemSmallPreviewData(
    val imageVector: ImageVector,
    val value: String,
    val isLoading: Boolean
)

private class DetailItemSmallPreviewProvider :
    PreviewParameterProvider<DetailItemSmallPreviewData> {
    override val values = sequenceOf(
        DetailItemSmallPreviewData(
            imageVector = Icons.Outlined.Person,
            value = "1887388193",
            isLoading = false
        ),
        DetailItemSmallPreviewData(
            imageVector = Icons.Outlined.Phone,
            value = "0455 100 000",
            isLoading = false
        ),
        DetailItemSmallPreviewData(
            imageVector = Icons.Outlined.Email,
            value = "example@example.com",
            isLoading = false
        ),
        DetailItemSmallPreviewData(
            imageVector = Icons.Outlined.BackHand,
            value = "2 hours",
            isLoading = true
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun DetailItemSmallPreview(
    @PreviewParameter(DetailItemSmallPreviewProvider::class) data: DetailItemSmallPreviewData
) {
    GPCTheme {
        DetailItemSmall(
            imageVector = data.imageVector,
            value = data.value,
            isLoading = data.isLoading
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailItemSmallChipPreview(
    @PreviewParameter(DetailItemSmallPreviewProvider::class) data: DetailItemSmallPreviewData
) {
    GPCTheme {
        DetailItemSmallChip(
            imageVector = data.imageVector,
            value = data.value,
            isLoading = data.isLoading
        )
    }
}

