package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.foundation.component.CollectOrderDetailsContent
import com.gpcasiapac.storesystems.feature.collect.presentation.component.CollectPickedAtChip
import com.gpcasiapac.storesystems.foundation.component.ListItemScaffold
import com.gpcasiapac.storesystems.foundation.component.ListItemToolbarScaffold
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CollectOrderItem(
    customerName: String,
    customerType: CustomerType,
    invoiceNumber: String,
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
    actions: @Composable RowScope.() -> Unit = {}
) {
    ListItemScaffold(
        modifier = modifier,
        contentPadding = contendPadding,
        toolbar = {
            CollectOrderToolbar(
                pickedAt = pickedAt,
                showAbsoluteTimeInitially = showAbsoluteTimeInitially,
                isLoading = isLoading,
                actions = actions
            )
        }
    ) {
        CollectOrderDetailsContent(
            customerName = customerName,
            customerType = customerType,
            invoiceNumber = invoiceNumber,
            webOrderNumber = webOrderNumber,
            isLoading = isLoading,
            contentPadding = PaddingValues(bottom = Dimens.Space.small)
        )
    }
}

@Composable
private fun RowScope.CollectOrderToolbar(
    pickedAt: Instant,
    showAbsoluteTimeInitially: Boolean,
    isLoading: Boolean = false,
    actions: @Composable RowScope.() -> Unit,
) {
    ListItemToolbarScaffold(
        actions = actions,
        overflowMenu = { dismiss ->
            DropdownMenuItem(
                text = { Text("Select all by customer") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.SelectAll,
                        contentDescription = "Select all"
                    )
                },
                onClick = { dismiss() }
            )
        }
    ) {
        CollectPickedAtChip(
            pickedAt = pickedAt,
            showAbsoluteTimeInitially = showAbsoluteTimeInitially,
            isLoading = isLoading
        )
    }
}


@Preview(name = "CollectOrderItem (Order List)", showBackground = true)
@Composable
private fun CollectOrderItemPreview() {
    GPCTheme {
        CollectOrderItem(
            customerName = "ABC Motorsports PTY Limited",
            customerType = CustomerType.B2B,
            invoiceNumber = "INV-123456",
            webOrderNumber = "WEB-987654",
            pickedAt = Instant.parse("2025-09-29T00:00:00Z"),
            isLoading = false
        )
    }
}
