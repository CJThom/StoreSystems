package com.gpcasiapac.storesystems.feature.collect.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.foundation.component.detailitem.DetailItemSmall
import com.gpcasiapac.storesystems.foundation.component.detailitem.DetailItemSmallChip
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


object ProductDetailsDefaults {
    val minWidth = 250.dp
    val contentPadding = PaddingValues(Dimens.Space.medium)
    val imageSize = 100.dp
}

/**
 * A product details card component that displays product information including
 * image, description, product code, price, and quantity.
 * Follows Material 3 design principles and uses semantic tokens from the theme.
 *
 * @param description The product name/description
 * @param sku The product code identifier
 * @param quantity The product quantity
 * @param modifier Modifier for the root composable
 */
@Composable
fun ProductDetails(
    description: String,
    sku: String,
    quantity: Int,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    minWidth: Dp = ProductDetailsDefaults.minWidth,
    contentPadding: PaddingValues = ProductDetailsDefaults.contentPadding
) {
    ListItemScaffold(
        modifier = modifier.widthIn(min = minWidth),
        contentPadding = contentPadding,
        toolbar = {
            ListItemToolbarScaffold(
                actions = {},
                overflowMenu = null
            ) {
                DetailItemSmallChip(
                    value = quantity.toString(),
                    imageVector = Icons.Outlined.ShoppingCart, // TODO: Get Deployed Code Icon,
                    isLoading = isLoading,
                )
            }
        }
    ) {
        ProductDetailsContent(
            description = description,
            sku = sku,
            isLoading = isLoading
        )
    }
}


/**
 * Product image container with light gray background matching the design
 */
@Composable
private fun ProductImageContainer(
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(Dimens.Space.extraSmall),
        contentAlignment = Alignment.Center
    ) {
        // Placeholder for product image
        // In a real implementation, this would be an AsyncImage or Image composable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.small)
                .placeholder(true)
        )
    }
}

// Preview functions
@Preview
@Composable
private fun ProductDetailsPreview() {
    GPCTheme {
        Surface {
            ProductDetails(
                description = "Dupli-Color Vinyl & Fabric Paint Gloss Black",
                sku = "A9442910",
                quantity = 2
            )
        }
    }
}

// Preview functions
@Preview
@Composable
private fun ProductDetailsLoadingPreview() {
    GPCTheme {
        Surface {
            ProductDetails(
                description = "Dupli-Color Vinyl & Fabric Paint Gloss Black",
                sku = "A9442910",
                quantity = 2,
                isLoading = true
            )
        }
    }
}

@Composable
private fun ProductDetailsContent(
    description: String,
    sku: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
    ) {
        ProductImageContainer(
            modifier = Modifier.size(ProductDetailsDefaults.imageSize)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall)
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(isLoading)
            )

            DetailItemSmall(
                value = sku,
                imageVector = Icons.Default.Search,
                isLoading = isLoading,
            )
        }
    }
}
