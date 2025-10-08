package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.sampleLineItemList
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ListSection(
    headline: String?,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    onViewMoreClick: (() -> Unit)? = null,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium),
    content: @Composable ColumnScope.() -> Unit,
) {

    Column(
        modifier = modifier.padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
    ) {

        if (headline != null) {
            // TODO: Create Headline component
            Text(
                text = headline,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.placeholder(isLoading)
            )
        }

        content()

        if (onViewMoreClick != null) {
            OutlinedButton(
                onClick = onViewMoreClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = if (isExpanded) {
                        "VIEW LESS"
                    } else {
                        "VIEW MORE"
                    },
                )
            }
        }
    }
}


@Preview
@Composable
fun ListSectionPreview() {
    GPCTheme {
        Surface {
            ListSection(
                headline = "Product list",
                isExpanded = false,
                onViewMoreClick = {}
            ) {
                sampleLineItemList.forEach { lineItem ->
                    ProductDetails(
                        description = lineItem.productDescription,
                        sku = lineItem.sku,
                        quantity = lineItem.quantity,
                        isLoading = false,
                        contentPadding = PaddingValues()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ListSectionLoadingPreview() {
    GPCTheme {
        Surface {
            ListSection(
                headline = "Product list",
                isExpanded = false,
                onViewMoreClick = {},
                isLoading = true
            ) {
                sampleLineItemList.forEach { lineItem ->
                    ProductDetails(
                        description = lineItem.productDescription,
                        sku = lineItem.sku,
                        quantity = lineItem.quantity,
                        isLoading = true,
                        contentPadding = PaddingValues()
                    )
                }
            }
        }
    }
}