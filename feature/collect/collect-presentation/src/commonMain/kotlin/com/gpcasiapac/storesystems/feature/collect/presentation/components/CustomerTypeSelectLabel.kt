package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CustomerTypeSelectLabel(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    icon: ImageVector,
    enabled: Boolean = true,
    label: String,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Show check icon if selected, otherwise show type icon
        if (!isSelected) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (!enabled) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(Dimens.Size.iconSmall)
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = when {
                !enabled -> MaterialTheme.colorScheme.outlineVariant
                isSelected -> MaterialTheme.colorScheme.onSecondaryContainer
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
@Preview
fun CustomerTypeSelectionLabel() {
    GPCTheme {
        Surface {
            CustomerTypeSelectLabel(
                modifier = Modifier,
                isSelected = false,
                icon = Icons.Default.Search,
                enabled = true,
                label = "Search"
            )
        }
    }
}
