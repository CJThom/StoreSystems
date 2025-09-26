package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductListSection(
    modifier: Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium),
        modifier = modifier
    ) {
        Text(
            text = "Product list",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
        )

        content()

        // View More Button
        OutlinedButton(
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            onClick = { /* Handle view more */ },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "VIEW MORE",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}

@Preview
@Composable
fun ProductListPreview() {
    GPCTheme {
        Surface {
            ProductListSection(
                modifier = Modifier.padding(Dimens.Space.medium),
            ) {
                ProductDetails(
                    productName = "Bendix Brake Pads Set - Ultimate 4WD - DB2060 ULT4WD",
                    productCode = "1A5563",
                    price = 37.99,
                    quantity = 8,
                    showPrice = true,
                    showQuantity = true
                )
            }
        }
    }
}