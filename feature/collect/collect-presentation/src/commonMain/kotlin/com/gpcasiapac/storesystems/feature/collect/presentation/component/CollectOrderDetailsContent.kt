package com.gpcasiapac.storesystems.feature.collect.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Web
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.foundation.component.detailitem.DetailItemSmall
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Extracted main content for Collect Order details consisting of customer name and identifiers.
 */
@Composable
fun CollectOrderDetailsContent(
    customerName: String,
    customerType: CustomerType,
    invoiceNumber: String,
    webOrderNumber: String?,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues()
) {
    Column(
        modifier = modifier.padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall)
    ) {
        CustomerName(
            customerName = customerName,
            customerType = customerType,
            modifier = Modifier.fillMaxWidth(),
            isLoading = isLoading
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DetailItemSmall(
                value = invoiceNumber,
                imageVector = Icons.Outlined.Receipt,
                isLoading = isLoading,
                modifier = Modifier
            )

            if (webOrderNumber != null) {
                DetailItemSmall(
                    value = webOrderNumber,
                    imageVector = Icons.Outlined.Web,
                    isLoading = isLoading,
                    modifier = Modifier
                )
            }
        }
    }
}


@Preview(name = "Details B2B with web", showBackground = true)
@Composable
private fun CollectOrderDetailsContentB2BPreview() {
    GPCTheme {
        CollectOrderDetailsContent(
            customerName = "ABC Motorsports PTY Limited",
            customerType = CustomerType.B2B,
            invoiceNumber = "INV-123456",
            webOrderNumber = "WEB-987654",
            isLoading = false
        )
    }
}

@Preview(name = "Details B2C no web", showBackground = true)
@Composable
private fun CollectOrderDetailsContentB2CPreview() {
    GPCTheme {
        CollectOrderDetailsContent(
            customerName = "Johnathan Citizenship",
            customerType = CustomerType.B2C,
            invoiceNumber = "INV-123456",
            webOrderNumber = null,
            isLoading = false
        )
    }
}
