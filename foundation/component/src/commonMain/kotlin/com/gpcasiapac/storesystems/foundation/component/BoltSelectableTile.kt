package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

@Composable
fun MBoltSelectableTile(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    subtitle: @Composable () -> Unit = {},
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = { onSelectionChanged(!isSelected) },
                role = Role.Checkbox
            ),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = Dimens.Stroke.thin,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp // Flat design as per Figma
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox section with background
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(vertical = Dimens.Space.extraSmall),
                contentAlignment = Alignment.Center
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onSelectionChanged,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.outline,
                        checkmarkColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }

            // Representative details section
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(Dimens.Space.medium),
                verticalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall)
            ) {
                title()
                subtitle()
            }
        }
    }
}

/**
 * Preview for RepresentativeCard in light theme
 */
@Preview(showBackground = true)
@Composable
private fun MBoltSelectableTilePreview() {
    GPCTheme {
        Surface {
            Column(
                modifier = Modifier.padding(Dimens.Space.medium),
                verticalArrangement = Arrangement.spacedBy(Dimens.Space.small)
            ) {
                MBoltSelectableTile(
                    modifier = Modifier.height(100.dp),
                    title = {
                        Text(
                            text = "John Doe",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    subtitle = {
                        Text(
                            text = "#922812357489654",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.inverseSurface
                        )
                    },
                    isSelected = true,
                    onSelectionChanged = { }
                )
                MBoltSelectableTile(
                    modifier = Modifier.height(100.dp),
                    title = {
                        Text(
                            text = "John Doe",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    subtitle = {
                        Text(
                            text = "#922812357489654",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.inverseSurface
                        )
                    },
                    isSelected = false,
                    onSelectionChanged = { }
                )
            }
        }
    }
}
