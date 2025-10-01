package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SingleChoiceSegmentedButtonRowScope
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SingleChoiceSegmentedButtonRowScope.MBoltSegmentedButton(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    index: Int,
    count: Int,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    SegmentedButton(
        /* TODO Remove Colors once updated to newer compose version with outline variant fix. */
        colors = SegmentedButtonDefaults.colors(
            activeContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            activeBorderColor = MaterialTheme.colorScheme.outlineVariant,
            inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant,
            disabledActiveBorderColor = MaterialTheme.colorScheme.outlineVariant,
            disabledInactiveBorderColor = MaterialTheme.colorScheme.outlineVariant,
            disabledActiveContentColor = MaterialTheme.colorScheme.outlineVariant,
            disabledInactiveContentColor = MaterialTheme.colorScheme.outlineVariant,
        ),
        icon = {
            Icon(
                imageVector = if (isActive) Icons.Default.Check else icon,
                contentDescription = label
            )
        },
        enabled = enabled,
        shape = SegmentedButtonDefaults.itemShape(
            index = index,
            count = count,
            baseShape = MaterialTheme.shapes.medium
        ),
        onClick = onClick,
        selected = isActive,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    )
}

@Preview
@Composable
private fun MBoltSegmentedButtonPreview() {
    var selectedIndex by remember { mutableStateOf(0) }
    val options = listOf("Option 1", "Option 2")

    Surface {
        SingleChoiceSegmentedButtonRow {
            options.forEachIndexed { index, label ->
                MBoltSegmentedButton(
                    label = label,
                    icon = Icons.Default.Home,
                    isActive = selectedIndex == index,
                    index = index,
                    count = options.size,
                    onClick = { selectedIndex = index }
                )
            }
        }
    }

}