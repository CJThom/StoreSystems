package com.gpcasiapac.storesystems.feature.history.presentation.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.foundation.placeholder
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import com.gpcasiapac.storesystems.foundation.component.InvoiceSummarySection
import com.gpcasiapac.storesystems.foundation.component.ListItemScaffold
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HistoryGroupedCard(
    customerName: String,
    invoiceNumbers: List<String>,
    time: Instant?,
    isLoading: Boolean = false,
    status: HistoryStatus = HistoryStatus.PENDING,
    onRetry: (() -> Unit)? = null,
) {
    ListItemScaffold(
        header = {
            HistoryHeader(
                modifier = Modifier
                    .placeholder(isLoading, shape = RoundedCornerShape(4.dp)),
                username = "65895231",
                time = time,
                enabledRetry = when (status) {
                    HistoryStatus.FAILED, HistoryStatus.REQUIRES_ACTION -> true
                    else -> false
                },
                onRetryClick = {
                    onRetry?.invoke()
                },
                status = status,
            )
        },
        content = {
            Column() {
                InvoiceSummarySection(
                    modifier = Modifier.padding(vertical = Dimens.Space.extraSmall)
                        .placeholder(isLoading),
                    invoices = invoiceNumbers,
                    customerName = customerName
                )
            }
        },
        toolbar = {

        }
    )
}

@Preview(showBackground = true)
@Composable
private fun HistoryGroupedCardLoadingPreview() {
    GPCTheme {
        HistoryGroupedCard(
            customerName = "",
            invoiceNumbers = emptyList(),
            time = Instant.fromEpochMilliseconds(1762423975),
            isLoading = true,
            status = HistoryStatus.PENDING,
            onRetry = null
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryGroupedCardSuccessPreview() {
    GPCTheme {
        HistoryGroupedCard(
            customerName = "Jane Customer",
            invoiceNumbers = listOf("INV-10001", "INV-10002"),
            time = Instant.fromEpochMilliseconds(1762423975),
            isLoading = false,
            status = HistoryStatus.COMPLETED,
            onRetry = null
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryGroupedCardInProgressPreview() {
    GPCTheme {
        HistoryGroupedCard(
            customerName = "John Shopper",
            invoiceNumbers = listOf("INV-12345"),
            time = Instant.fromEpochMilliseconds(1762423975),
            isLoading = false,
            status = HistoryStatus.IN_PROGRESS,
            onRetry = null
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryGroupedCardFailedPreview() {
    GPCTheme {
        HistoryGroupedCard(
            customerName = "Alex Customer",
            invoiceNumbers = listOf("INV-99999"),
            time = Instant.fromEpochMilliseconds(1762423975),
            isLoading = false,
            status = HistoryStatus.FAILED,
            onRetry = {}
        )
    }
}