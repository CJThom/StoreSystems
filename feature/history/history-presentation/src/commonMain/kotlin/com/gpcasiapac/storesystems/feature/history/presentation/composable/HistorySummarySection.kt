package com.gpcasiapac.storesystems.feature.history.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.kotlin.extension.toLocalDateTimeString
import com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlin.time.Clock

@Composable
fun SummarySection(
    modifier: Modifier = Modifier,
    item: HistoryItem
) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "Status",
                style = MaterialTheme.typography.titleSmall
            )
            HistoryStatusText(status = item.status)
        }

        Spacer(Modifier.height(Dimens.Space.medium))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CalendarMonth, "SubmittedDate")
            KeyValueRow(label = "Submitted", value = item.timestamp.toLocalDateTimeString())
        }
        Spacer(Modifier.height(Dimens.Space.small))
        val requestId = when (item) {
            is CollectHistoryItem -> item.requestId
        }
        KeyValueRow(label = "Request ID", value = requestId)
    }
}

@Preview
@Composable
fun CollectHistoryItemPreview() {
    GPCTheme {
        Surface {
            SummarySection(
                modifier = Modifier.padding(10.dp),
                item = CollectHistoryItem(
                    id = "",
                    entityId = "",
                    status = HistoryStatus.COMPLETED,
                    timestamp = Clock.System.now(),
                    attempts = 0,
                    lastError = null,
                    priority = 0,
                    submittedBy = "Preview User",
                    requestId = "REQ-123",
                    metadata = listOf(
                        HistoryMetadata.CollectMetadata(
                            invoiceNumber = "123456",
                            orderNumber = "REF123",
                            webOrderNumber = "123456",
                            createdDateTime = Clock.System.now(),
                            invoiceDateTime = Clock.System.now(),
                            customerNumber = "12345678",
                            customerType = "B2B",
                            name = "John Doe",
                            phone = "123456789"
                        )
                    )
                )
            )
        }
    }
}