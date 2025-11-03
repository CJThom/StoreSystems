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
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.ProductLinePreview
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureSummaryState
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

@Composable
fun SignatureOrderSummary(
    summary: SignatureSummaryState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium)
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.small)
    ) {
        when (summary) {
            is SignatureSummaryState.Single -> SingleOrderSummaryCard(
                invoiceNumber = summary.invoiceNumber,
                customerName = summary.customerName,
                totalQuantity = summary.totalQuantity,
                productLines = summary.productPreview?.lines ?: emptyList(),
                productRemainingCount = summary.productPreview?.remainingCount ?: 0,
            )
            is SignatureSummaryState.Multi -> MultiOrderSummaryCard(
                invoiceJoinedText = summary.invoicePreview.joinedText,
                invoiceRemainingCount = summary.invoicePreview.remainingCount,
                orderCount = summary.orderCount,
                totalQuantity = summary.totalQuantity,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SingleOrderSummaryCard(
    invoiceNumber: String,
    customerName: String,
    totalQuantity: Int,
    productLines: List<ProductLinePreview>,
    productRemainingCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
    ) {
        // Header
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
                text = invoiceNumber,
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
                text = customerName,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.weight(0.75f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Quantity",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "x$totalQuantity",
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }

        if (productLines.isNotEmpty() || productRemainingCount > 0) {
            HorizontalDivider()
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall)) {
                productLines.forEach { line ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = "- ${line.description}",
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
                if (productRemainingCount > 0) {
                    Text(
                        text = "+$productRemainingCount more",
                        maxLines = 1,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MultiOrderSummaryCard(
    invoiceJoinedText: String,
    invoiceRemainingCount: Int,
    orderCount: Int,
    totalQuantity: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
    ) {
        val invoiceAnnotated = buildAnnotatedString {
            withStyle(style = MaterialTheme.typography.titleMedium.toSpanStyle()) {
                append("Invoices: ")
            }
            append(invoiceJoinedText)
            if (invoiceRemainingCount > 0) {
                append(" +${invoiceRemainingCount} more")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = invoiceAnnotated,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.85f)
            )
            Text(
                text = "x$orderCount",
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
                text = "x$totalQuantity",
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@Preview(
    name = "SignatureOrderSummary • Single",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5L,
    widthDp = 360
)
@Composable
private fun SignatureOrderSummarySinglePreview() {
    val summary = SignatureSummaryState.Single(
        invoiceNumber = "INV-000123",
        customerName = "Jane Doe",
        totalQuantity = 5,
        productPreview = com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.ProductPreview(
            lines = listOf(
                com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.ProductLinePreview("Widget A", 2),
                com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.ProductLinePreview("Gadget B", 3),
            ),
            remainingCount = 1
        )
    )
    GPCTheme { SignatureOrderSummary(summary = summary) }
}

@Preview(
    name = "SignatureOrderSummary • Multi",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5L,
    widthDp = 360
)
@Composable
private fun SignatureOrderSummaryMultiPreview() {
    val summary = SignatureSummaryState.Multi(
        orderCount = 3,
        invoicePreview = com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.InvoicePreview(
            joinedText = "INV1, INV2, INV3",
            remainingCount = 2
        ),
        totalQuantity = 12
    )
    GPCTheme { SignatureOrderSummary(summary = summary) }
}