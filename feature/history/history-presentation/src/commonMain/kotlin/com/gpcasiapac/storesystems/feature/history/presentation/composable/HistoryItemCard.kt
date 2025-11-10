package com.gpcasiapac.storesystems.feature.history.presentation.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import com.gpcasiapac.storesystems.feature.history.presentation.mapper.toCustomerTypeParam
import com.gpcasiapac.storesystems.foundation.component.CollectOrderDetailsContent
import com.gpcasiapac.storesystems.foundation.component.ListItemScaffold
import com.gpcasiapac.storesystems.foundation.component.ListItemToolbarScaffold
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Main composable for rendering a history item card.
 * Delegates to specific composables based on HistoryItem subtype.
 */
@Composable
fun HistoryItemCard(
    item: HistoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (item) {
        is CollectHistoryItem -> CollectHistoryItemCard(
            item = item,
            onClick = onClick,
            modifier = modifier
        )
    }
}

/**
 * Composable for history items with collect metadata.
 * Shows order and customer information.
 */
@Composable
private fun CollectHistoryItemCard(
    item: CollectHistoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val metadata = item.metadata.maxByOrNull { it.invoiceDateTime } ?: item.metadata.firstOrNull()
    if (metadata == null) return

    ListItemScaffold(
        modifier = modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(),
        toolbar = {

        }
    ) {
        CollectOrderDetailsContent(
            customerName = metadata.getCustomerDisplayName(),
            customerType = metadata.customerType.toCustomerTypeParam(),
            invoiceNumber = metadata.invoiceNumber,
            webOrderNumber = metadata.webOrderNumber,
            isLoading = false,
            contentPadding = PaddingValues(bottom = Dimens.Space.small)
        )
    }
}

