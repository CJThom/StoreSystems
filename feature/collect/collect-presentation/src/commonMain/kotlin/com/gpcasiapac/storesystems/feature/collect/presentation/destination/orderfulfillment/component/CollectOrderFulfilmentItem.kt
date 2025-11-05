package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.foundation.component.CollectOrderDetailsContent
import com.gpcasiapac.storesystems.feature.collect.presentation.component.CollectPickedAtChip
import com.gpcasiapac.storesystems.feature.collect.presentation.mapper.toParam
import com.gpcasiapac.storesystems.foundation.component.ListItemScaffold
import com.gpcasiapac.storesystems.foundation.component.ListItemToolbarScaffold
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlin.time.Instant

/**
 * Collect order item for the Order Fulfilment screen.
 * - Shows the common picked-at chip on the left of the toolbar.
 * - Shows a delete action on the right.
 * - No overflow menu.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CollectOrderFulfilmentItem(
    customerName: String,
    customerType: CustomerType,
    invoiceNumber: InvoiceNumber,
    webOrderNumber: String?,
    pickedAt: Instant,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    contendPadding: PaddingValues = PaddingValues(
        start = Dimens.Space.medium,
        top = Dimens.Space.medium,
        end = Dimens.Space.medium,
        bottom = Dimens.Space.small
    ),
    showAbsoluteTimeInitially: Boolean = false,
    onDelete: () -> Unit = {},
) {
    ListItemScaffold(
        modifier = modifier,
        contentPadding = contendPadding,
        toolbar = {
            FulfilmentToolbar(
                pickedAt = pickedAt,
                showAbsoluteTimeInitially = showAbsoluteTimeInitially,
                isLoading = isLoading,
                onDelete = onDelete
            )
        }
    ) {
        CollectOrderDetailsContent(
            customerName = customerName,
            customerType = customerType.toParam(),
            invoiceNumber = invoiceNumber,
            webOrderNumber = webOrderNumber,
            isLoading = isLoading
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun RowScope.FulfilmentToolbar(
    pickedAt: Instant,
    showAbsoluteTimeInitially: Boolean,
    isLoading: Boolean = false,
    onDelete: () -> Unit,
) {
    ListItemToolbarScaffold(
        actions = {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(IconButtonDefaults.extraSmallContainerSize())
            ) {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete")
            }
        },
        overflowMenu = null
    ) {
        CollectPickedAtChip(
            pickedAt = pickedAt,
            showAbsoluteTimeInitially = showAbsoluteTimeInitially,
            isLoading = isLoading
        )
    }
}


@Preview(name = "CollectOrderFulfilmentItem", showBackground = true)
@Composable
private fun CollectOrderFulfilmentItemPreview() {
    GPCTheme {
        CollectOrderFulfilmentItem(
            customerName = "ABC Motorsports PTY Limited",
            customerType = CustomerType.B2B,
            invoiceNumber = InvoiceNumber("INV-123456"),
            webOrderNumber = "WEB-987654",
            pickedAt = Instant.parse("2025-09-29T00:00:00Z"),
            isLoading = false,
            onDelete = {}
        )
    }
}
