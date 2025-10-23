package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

/**
 * Multi-select bottom bar component that displays selection controls and action buttons.
 *
 * @param selectedCount The number of selected items
 * @param isSelectAllChecked Whether the select all checkbox is checked
 * @param onSelectAllToggle Callback when select all checkbox is toggled
 * @param onCancelClick Callback when cancel button is clicked
 * @param onSelectClick Callback when select button is clicked
 * @param modifier Modifier to be applied to the component
 */
@Composable
fun MultiSelectBottomBar(
    modifier: Modifier = Modifier,
    selectedCount: Int = 0,
    isSelectAllChecked: Boolean = false,
    onSelectAllToggle: (Boolean) -> Unit = {},
    onCancelClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
) {

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shadowElevation = 16.dp
    ) {
        Column {
            // Top border
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(
                        WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)
                    )
                    .padding(horizontal = Dimens.Space.medium, vertical = Dimens.Space.small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Select All Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small)
                ) {
                    Checkbox(
                        checked = isSelectAllChecked,
                        onCheckedChange = onSelectAllToggle
                    )

                    Text(
                        text = "SELECT ALL",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Action Buttons Section
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cancel Button
                    TextButton(
                        onClick = onCancelClick,
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text(
                            text = "CANCEL",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Select Button
                    Button(
                        onClick = onSelectClick,
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = buildString {
                                append("SELECT ")
                                if (selectedCount > 0) {
                                    append(selectedCount.toString())
                                }
                            },
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (selectedCount > 0) FontWeight.Bold else FontWeight.Medium,
                            ),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MultiSelectBottomBarPreview() {
    GPCTheme {
        MultiSelectBottomBar(
            selectedCount = 2,
            isSelectAllChecked = false
        )
    }
}
