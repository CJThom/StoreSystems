package com.gpcasiapac.storesystems.feature.history.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import com.gpcasiapac.storesystems.foundation.component.InvoiceSummarySection
import com.gpcasiapac.storesystems.foundation.component.ListItemScaffold
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HistoryGroupedCard(
    customerName: String,
    invoiceNumbers: List<String>,
    time: String,
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
                timeAgo = Clock.System.now(),
                enabledRetry = when (status) {
                    HistoryStatus.FAILED, HistoryStatus.REQUIRES_ACTION -> true
                    else -> false
                },
                onRetryClick = {
                    onRetry?.invoke()
                },
                isRetrying = status == HistoryStatus.IN_PROGRESS,
                status = status,
            )
        },
        content = {
            Column {
                if (isLoading) {
                    // Skeleton blocks approximating text content
                    Box(
                        Modifier
                            .fillMaxWidth(0.6f)
                            .height(16.dp)
                            .placeholder(true, shape = RoundedCornerShape(4.dp))
                    )
                    Spacer(Modifier.height(Dimens.Space.small))
                    Box(
                        Modifier
                            .fillMaxWidth(0.8f)
                            .height(14.dp)
                            .placeholder(true, shape = RoundedCornerShape(4.dp))
                    )
                    Spacer(Modifier.height(Dimens.Space.extraSmall))
                    Box(
                        Modifier
                            .fillMaxWidth(0.5f)
                            .height(14.dp)
                            .placeholder(true, shape = RoundedCornerShape(4.dp))
                    )
                } else {
                    InvoiceSummarySection(
                        invoices = invoiceNumbers,
                        customerName = customerName
                    )
                }
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
            time = "",
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
            time = "2h ago",
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
            time = "Just now",
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
            time = "1d ago",
            isLoading = false,
            status = HistoryStatus.FAILED,
            onRetry = {}
        )
    }
}