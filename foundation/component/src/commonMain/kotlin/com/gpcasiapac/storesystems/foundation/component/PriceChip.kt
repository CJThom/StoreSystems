package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Delivery time chip component
 */
@Composable
fun PriceChip(
    price: Double,
    modifier: Modifier = Modifier
) {
    DetailItemChip(modifier = modifier) {
        Text("$")

        Text(
            text = price.toString(),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

@Preview
@Composable
fun PriceChipPreview() {
    GPCTheme {
        PriceChip(
            price = 97.58,
            modifier = Modifier.padding(Dimens.Space.medium)
        )
    }
}