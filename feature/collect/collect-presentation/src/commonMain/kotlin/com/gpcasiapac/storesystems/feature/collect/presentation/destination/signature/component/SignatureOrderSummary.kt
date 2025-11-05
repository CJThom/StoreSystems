package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.component


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.ProductLinePreview
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureSummaryState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.preview.SignatureSummaryPreviewData
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme


object SignatureOrderSummaryDefaults {

    val maxHeight = 75.dp

}

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
        // verticalArrangement = Arrangement.spacedBy(Dimens.Space.small)
    ) {
        when (summary) {
            is SignatureSummaryState.Single -> SingleOrderSummaryCard(
                invoiceNumber = summary.invoiceNumber,
                customerName = summary.customerName,
                totalQuantity = summary.totalQuantity
            )

            is SignatureSummaryState.Multi -> MultiOrderSummaryCard(
                invoiceJoinedText = summary.joinedText,
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.height(SignatureOrderSummaryDefaults.maxHeight),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
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
                verticalAlignment = Alignment.CenterVertically,
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
        }

        Spacer(Modifier.weight(1F))

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
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MultiOrderSummaryCard(
    invoiceJoinedText: String,
    orderCount: Int,
    totalQuantity: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.height(SignatureOrderSummaryDefaults.maxHeight),
    ) {
        val invoiceAnnotated = buildAnnotatedString {
            withStyle(style = MaterialTheme.typography.titleMedium.toSpanStyle()) {
                append("Invoices: ")
            }
            append(invoiceJoinedText)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = invoiceAnnotated,
                style = MaterialTheme.typography.bodyLarge,
                minLines = 2,
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

        Spacer(Modifier.weight(1F))

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


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ProductLinesPreview(
    productLines: List<ProductLinePreview>,
    productRemainingCount: Int,
    modifier: Modifier = Modifier
) {
    if (productLines.isNotEmpty() || productRemainingCount > 0) {
        HorizontalDivider()
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall)
        ) {
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

private class SignatureOrderSummaryPreviewProvider :
    PreviewParameterProvider<SignatureSummaryState> {
    override val values: Sequence<SignatureSummaryState>
        get() = SignatureSummaryPreviewData.summaries.asSequence()
}

@Preview(
    name = "SignatureOrderSummary • Parametrized",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5L,
    widthDp = 360
)
@Composable
private fun SignatureOrderSummaryPreview(
    @PreviewParameter(SignatureOrderSummaryPreviewProvider::class) summary: SignatureSummaryState
) {
    GPCTheme { SignatureOrderSummary(summary = summary) }
}

