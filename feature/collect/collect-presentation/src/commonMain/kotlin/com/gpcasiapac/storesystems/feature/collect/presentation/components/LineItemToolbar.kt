package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

/**
 * LineItemToolbar component that displays a number picker and action buttons
 * Based on the Figma design with theme-driven styling
 */
@Composable
fun LineItemToolbar(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onShelvesClick: () -> Unit,
    onMoreOptionsClick: () -> Unit,
    modifier: Modifier = Modifier,
    minQuantity: Int = 0,
    maxQuantity: Int = Int.MAX_VALUE
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Space.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Number Picker Section
        NumberPicker(
            quantity = quantity,
            onQuantityChange = onQuantityChange,
            minQuantity = minQuantity,
            maxQuantity = maxQuantity,
            modifier = Modifier
                .height(56.dp)
                .width(134.dp)
        )

        // Actions Section
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(140.dp)
        ) {
            // Shelves Icon Button
            IconButton(
                onClick = onShelvesClick,
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Shelves",
                        modifier = Modifier.size(20.dp),
                        tint = colors.onSurface
                    )
                }
            }

            // More Options Icon Button
            IconButton(
                onClick = onMoreOptionsClick,
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        modifier = Modifier.size(20.dp),
                        tint = colors.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Number picker component with decrease and increase buttons
 */
@Composable
private fun NumberPicker(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    minQuantity: Int,
    maxQuantity: Int,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(1000.dp))
            .background(colors.secondaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.Space.extraSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Decrease Button
            IconButton(
                onClick = {
                    if (quantity > minQuantity) {
                        onQuantityChange(quantity - 1)
                    }
                },
                enabled = quantity > minQuantity,
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colors.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Decrease quantity",
                        modifier = Modifier.size(24.dp),
                        tint = colors.onSecondaryContainer
                    )
                }
            }

            // Quantity Display
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = colors.onSecondaryContainer
            )

            // Increase Button
            IconButton(
                onClick = {
                    if (quantity < maxQuantity) {
                        onQuantityChange(quantity + 1)
                    }
                },
                enabled = quantity < maxQuantity,
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colors.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase quantity",
                        modifier = Modifier.size(24.dp),
                        tint = colors.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun LineItemToolbarPreview() {
    GPCTheme {
        Surface {
            var quantity by remember { mutableIntStateOf(3) }

            LineItemToolbar(
                quantity = quantity,
                onQuantityChange = { quantity = it },
                onShelvesClick = { /* Handle shelves click */ },
                onMoreOptionsClick = { /* Handle more options click */ },
                modifier = Modifier.padding(Dimens.Space.medium)
            )
        }
    }
}
