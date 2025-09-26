package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.gpcasiapac.storesystems.foundation.design_system.Dimens

@Composable
fun SingleChoiceSegmentedButtonRowScope.CustomerTypeSegmentedButton(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    index: Int,
    count: Int,
    enabled: Boolean = true,
    onClick: () -> Unit,
    ) {
    SegmentedButton(
        colors = SegmentedButtonDefaults.colors(
            activeContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            activeBorderColor = MaterialTheme.colorScheme.outlineVariant,
            inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant,
            disabledActiveBorderColor = MaterialTheme.colorScheme.outlineVariant,
            disabledInactiveBorderColor = MaterialTheme.colorScheme.outlineVariant,
            disabledActiveContentColor = MaterialTheme.colorScheme.outlineVariant,
            disabledInactiveContentColor = MaterialTheme.colorScheme.outlineVariant,
        ),
        enabled = enabled,
        shape = SegmentedButtonDefaults.itemShape(
            index = index,
            count = count,
            baseShape = RoundedCornerShape(Dimens.Space.medium)
        ),
        onClick = onClick,
        selected = isActive,
        //contentPadding = SegmentedButtonDefaults.ContentPadding,
        label = {
            CustomerTypeSelectLabel(
                isSelected = isActive,
                icon = icon,
                label = label,
                enabled = enabled,
            )
        }
    )
}