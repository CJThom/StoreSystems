package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRowScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.gpcasiapac.storesystems.foundation.design_system.Dimens

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