package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SearchResultItemSuggestion(
    text: String,
    icon: ImageVector,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { /* Handle item click */ }
            .padding(Dimens.Space.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = colors.onSurface,
            modifier = Modifier.size(Dimens.Size.iconMedium)
        )

        Text(
            text = text,
            style = typography.bodyLarge,
            color = colors.onSurface,
            modifier = Modifier.weight(1f)
        )

        OutlinedButton(
            onClick = onFilterClick,
            modifier = Modifier.height(32.dp),
            contentPadding = PaddingValues(horizontal = Dimens.Space.medium, vertical = 0.dp),
            border = ButtonDefaults.outlinedButtonBorder(
                enabled = true
            ).copy(
                width = 0.75.dp
            )
        ) {
            Text(
                text = "FILTER",
                style = typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = colors.onSurface
            )
        }
    }
}

@Preview
@Composable
fun SearchResultItemSuggestionPreview() {
    GPCTheme {
        Surface {
            SearchResultItemSuggestion(
                text = "1025685224889",
                icon = Icons.Default.Phone,
                onFilterClick = {

                }
            )
        }
    }
}