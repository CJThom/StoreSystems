package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureOrderState
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

//object SignatureOrderSummaryDefaults {
//    // Centralized weights to avoid magic numbers and keep visuals unchanged
//    const val LabelWeight = 0.35f
//    const val ValueWeight = 0.75f
//}


@Composable
fun SignatureOrderSummary(
    orders: List<SignatureOrderState>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium)
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.small)
    ) {

        if (orders.size == 1) {
            SingleOrderSummaryCard(order = orders.first())
        } else {
            MultiOrderSummaryCard(orders = orders)
        }

    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SingleOrderSummaryCard(
    order: SignatureOrderState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
    ) {
        SingleOrderSummaryHeader(order = order)
        SingleOrderQuantityRow(order = order)
        // If needed in future: SingleOrderProductsPreview(order)
    }
}

// ——— Inlined private components ———

@Composable
private fun SingleOrderSummaryHeader(
    order: SignatureOrderState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.small)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Invoice",
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.weight(0.35f)
            )
            Text(
                text = order.invoiceNumber,
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.75f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Customer",
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.weight(0.35f)
            )
            Text(
                text = order.customerName,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.weight(0.75f)
            )
        }
    }
}

@Composable
private fun SingleOrderQuantityRow(
    order: SignatureOrderState,
    modifier: Modifier = Modifier,
) {
    val totalQty = order.lineItems.sumOf { it.quantity }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Quantity",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "x${totalQty}",
            style = MaterialTheme.typography.titleMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

@Composable
private fun SingleOrderProductsPreview(
    order: SignatureOrderState,
    modifier: Modifier = Modifier,
) {
    // Simple product lines summary
    HorizontalDivider()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall)
    ) {
        val previewLines = order.lineItems.take(3)
        previewLines.forEach { line ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "- ${line.productDescription}",
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.weight(0.85f)
                )
                Text(
                    text = "x${line.quantity}",
                    style = MaterialTheme.typography.labelLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.weight(0.15f),
                    textAlign = TextAlign.End,
                )
            }
        }
        val remaining = order.lineItems.size - previewLines.size
        if (remaining > 0) {
            Text(
                text = "+$remaining more",
                maxLines = 1,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MultiOrderSummaryCard(
    orders: List<SignatureOrderState>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
    ) {
        val invoiceNumbers = orders.joinToString(", ") { it.invoiceNumber }
        val totalQty = orders.sumOf { order -> order.lineItems.sumOf { it.quantity } }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = MaterialTheme.typography.titleMedium.toSpanStyle()) {
                        append("Invoices: ")
                    }
                    append(invoiceNumbers)
                },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(0.85f)
            )
            Text(
                text = "x${orders.size}",
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.15f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total quantity",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "x${totalQty}",
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@Preview(
    name = "SignatureOrderSummary • Scenarios",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5L,
    widthDp = 360
)
@Composable
private fun SignatureOrderSummaryPreview(
    @PreviewParameter(SignatureOrderSummaryOrdersProvider::class) orders: List<SignatureOrderState>
) {
    GPCTheme {
        SignatureOrderSummary(
            orders = orders
        )
    }
}