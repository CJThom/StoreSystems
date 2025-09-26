package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A card component that displays order information with a selectable checkbox.
 * Follows Material 3 design principles and uses semantic tokens from the theme.
 *
 * @param customerName The name of the customer
 * @param orderDetails List of order detail items to display
 * @param deliveryTime The estimated delivery time
 * @param isSelected Whether the checkbox is selected
 * @param onSelectionChanged Callback when the selection state changes
 * @param modifier Modifier for the root composable
 */
@Composable
fun CheckboxOrderCard(
    isSelectable: Boolean,
    customerName: String,
    isBusiness: Boolean,
    orderDetails: @Composable RowScope.() -> Unit,
    deliveryTime: String,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(Dimens.Stroke.thin, MaterialTheme.colorScheme.outlineVariant),
                shape = MaterialTheme.shapes.small,
            )
            .toggleable(
                value = isSelected,
                onValueChange = onSelectionChanged,
                role = Role.Checkbox
            ),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox section with background
            AnimatedVisibility(isSelectable) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                        )
                        .padding(Dimens.Space.extraSmall)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = onSelectionChanged
                    )
                }
            }
            // Content section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.Space.medium),
                verticalArrangement = Arrangement.spacedBy(Dimens.Space.small)
            ) {
                if (isBusiness) {
                    // Business name with icon
                    BusinessNameSection(
                        customerName = customerName,
                    )
                } else {
                    // Customer name with icon
                    CustomerNameSection(
                        customerName = customerName,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    orderDetails()
                }

                // Delivery time
                DeliveryTimeChip(
                    deliveryTime = deliveryTime,
                )
            }
        }
    }
}

@Preview
@Composable
private fun CheckboxOrderCardPreview() {
    GPCTheme {
        Surface {
            var isSelected by remember { mutableStateOf(true) }

            CheckboxOrderCard(
                isSelectable = true,
                customerName = "Johnathan Citizenship",
                deliveryTime = "2 hours",
                isSelected = isSelected,
                onSelectionChanged = { isSelected = it },
                isBusiness = true,
                modifier = Modifier.padding(Dimens.Space.medium),
                orderDetails = {
                    OrderDetailRow(
                        text = "1000000000",
                        icon = Icons.Outlined.Receipt,
                        modifier = Modifier.weight(1f),
                    )
                    OrderDetailRow(
                        text = "1000000000",
                        icon = Icons.Outlined.Phone,
                        modifier = Modifier.weight(1f),
                    )
                }
            )
        }
    }
}
