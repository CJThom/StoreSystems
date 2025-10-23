package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.mapper.toSignatureOrderState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureOrderState
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

@Composable
fun SignatureOrderSummary(
    orders: List<SignatureOrderState>,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium)
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.small)
    ) {
//        Text(
//            text = "Order details",
//            style = MaterialTheme.typography.titleLarge
//        )

        if (orders.size == 1) {
            SingleOrderSummaryCard(order = orders.first(), onViewDetails = onViewDetails)
        } else {
            MultiOrderSummaryCard(orders = orders, onViewDetails = onViewDetails)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SingleOrderSummaryCard(
    order: SignatureOrderState,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.small)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Invoice",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = order.invoiceNumber,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Customer",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = order.customerName,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Simple product lines summary
        HorizontalDivider()

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall)) {
            val previewLines = order.lineItems.take(3)
            previewLines.forEach { line ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = line.productDescription,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "x${line.quantity}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            val remaining = order.lineItems.size - previewLines.size
            if (remaining > 0) {
                Text(
                    text = "+$remaining more",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        OutlinedButton(
            onClick = onViewDetails,
            modifier = Modifier
                .fillMaxWidth()
                .height(ButtonDefaults.ExtraSmallContainerHeight)
        ) {
            Text(
                text = "VIEW DETAILS",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MultiOrderSummaryCard(
    orders: List<SignatureOrderState>,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.small)
    ) {
        val invoiceNumbers = orders.joinToString(", ") { it.invoiceNumber }
        val totalQty = orders.sumOf { order -> order.lineItems.sumOf { it.quantity } }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small)
        ) {
            Text(
                text = "Invoices:",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = invoiceNumbers,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total quantity"
            )
            Text(
                text = totalQty.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        OutlinedButton(
            onClick = onViewDetails,
            modifier = Modifier
                .fillMaxWidth()
                .height(ButtonDefaults.ExtraSmallContainerHeight)
        ) {
            Text(
                text = "VIEW DETAILS",
                style = MaterialTheme.typography.bodyMedium
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
            orders = orders,
            onViewDetails = {}
        )
    }
}
