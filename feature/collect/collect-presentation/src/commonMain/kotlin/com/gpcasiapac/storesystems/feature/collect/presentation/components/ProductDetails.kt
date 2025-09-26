package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.foundation.component.PriceChip
import com.gpcasiapac.storesystems.foundation.component.QuantityChip
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A product details card component that displays product information including
 * image, description, product code, price, and quantity.
 * Follows Material 3 design principles and uses semantic tokens from the theme.
 *
 * @param productName The product name/description
 * @param productCode The product code identifier
 * @param price The product price
 * @param quantity The product quantity
 * @param modifier Modifier for the root composable
 * @param showPrice Whether to display the price
 * @param showQuantity Whether to display the quantity
 */
@Composable
fun ProductDetails(
    productName: String,
    productCode: String,
    price: Double,
    quantity: Int,
    modifier: Modifier = Modifier,
    showPrice: Boolean = true,
    showQuantity: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
    ) {
        ProductImageContainer(
            modifier = Modifier.size(100.dp)
        )

        // Product Information
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall)
        ) {
            // Product Name/Description
            ProductTitle(productName = productName)

            // Product Code with Icon
            ProductCodeRow(
                productCode = productCode,
            )

            // Price and Quantity Row
            if (showPrice || showQuantity) {
                PriceQuantityRow(
                    price = price,
                    quantity = quantity,
                    showPrice = showPrice,
                    showQuantity = showQuantity,
                )
            }
        }
    }
}

@Composable
private fun ProductTitle(productName: String) {
    Text(
        text = productName,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Medium
        ),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.fillMaxWidth()
    )
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
                .background(Color.Gray)
        )
    }
}

/**
 * Product code row with search icon
 */
@Composable
private fun ProductCodeRow(
    productCode: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Product code",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Dimens.Size.iconSmall)
        )

        Text(
            text = productCode,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Price and quantity row with appropriate styling
 */
@Composable
private fun PriceQuantityRow(
    price: Double,
    quantity: Int,
    showPrice: Boolean,
    showQuantity: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showPrice) {
            // Price with yellow background chip
            PriceChip(
                price = price,
            )
        }

        if (showQuantity) {
            QuantityChip(
                quantity = quantity,
            )
        }
    }
}

// Preview functions
@Preview
@Composable
private fun ProductDetailsPreview() {
    GPCTheme {
        Surface {
            ProductDetails(
                productName = "Dupli-Color Vinyl & Fabric Paint Gloss Black",
                productCode = "A9442910",
                price = 37.99,
                quantity = 2,
                modifier = Modifier.padding(Dimens.Space.medium)
            )
        }
    }
}
