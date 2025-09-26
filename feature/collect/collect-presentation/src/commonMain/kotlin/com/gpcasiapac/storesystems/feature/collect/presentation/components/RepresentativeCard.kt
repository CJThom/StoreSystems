package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.foundation.component.MBoltSelectableTile
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Representative Card component that displays representative details with a checkbox.
 * Matches the Figma design for horizontal card layout with selection capability.
 *
 * @param name The representative's name
 * @param customerNumber The customer number (should include # prefix)
 * @param isSelected Whether the card is currently selected
 * @param onSelectionChanged Callback when selection state changes
 * @param modifier Modifier to be applied to the card
 */
@Composable
fun RepresentativeCard(
    name: String,
    customerNumber: String,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    MBoltSelectableTile(
        modifier = modifier,
        title = {
            // Representative name
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        isSelected = isSelected,
        onSelectionChanged = onSelectionChanged,
        subtitle = {
            // Customer number
            Text(
                text = customerNumber,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.inverseSurface
            )
        }
    )
}

/**
 * Preview for RepresentativeCard in light theme
 */
@Preview
@Composable
private fun RepresentativeCardLightPreview() {
    GPCTheme {
        Surface {
            Column(
                modifier = Modifier.padding(Dimens.Space.medium),
                verticalArrangement = Arrangement.spacedBy(Dimens.Space.small)
            ) {
                RepresentativeCard(
                    modifier = Modifier.height(80.dp),
                    name = "John Doe",
                    customerNumber = "#9288180049912",
                    isSelected = true,
                    onSelectionChanged = { }
                )

                RepresentativeCard(
                    modifier = Modifier.height(80.dp),
                    name = "Jane Smith",
                    customerNumber = "#9288180049913",
                    isSelected = false,
                    onSelectionChanged = { }
                )
            }
        }
    }
}
