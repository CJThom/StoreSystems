package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.foundation.component.CustomerName
import com.gpcasiapac.storesystems.foundation.component.detailitem.DetailItemMedium
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

/**
 * Customer Details component that displays customer information in a card format.
 * Matches the Figma design with customer name, B2C icon, and detail items for customer number and phone.
 *
 * @param customerName The customer's full name
 * @param customerNumber The customer identification number
 * @param phoneNumber The customer's phone number
 * @param modifier Modifier to be applied to the component
 */
@Composable
fun CustomerDetails(
    customerName: String,
    customerNumber: String,
    phoneNumber: String?,
    customerType: CustomerType,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues()
) {
    Column(
        modifier = modifier.padding(contentPadding)
    ) {

        CustomerName(
            customerName = customerName,
            customerType = customerType,
            modifier = Modifier,
            contentPadding = PaddingValues(Dimens.Space.medium)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.Space.medium),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Space.large)
        ) {

            DetailItemMedium(
                imageVector = Icons.Outlined.Receipt,
                label = "Customer Number",
                value = customerNumber,
                modifier = Modifier.weight(1f),
                isLoading = isLoading
            )

            DetailItemMedium(
                imageVector = Icons.Outlined.Call,
                label = "Phone",
                value = phoneNumber ?: "-",
                modifier = Modifier.weight(1f),
                isLoading = isLoading
            )

        }
    }
}

@Preview
@Composable
private fun CustomerDetailsB2BPreview() {
    GPCTheme {
        Surface {
            CustomerDetails(
                customerName = "Johnathan Citizenship",
                customerNumber = "1887388193",
                phoneNumber = "0455 100 000",
                customerType = CustomerType.B2B
            )
        }
    }
}

@Preview
@Composable
private fun CustomerDetailsB2CPreview() {
    GPCTheme {
        Surface {
            CustomerDetails(
                customerName = "Johnathan Citizenship",
                customerNumber = "1887388193",
                phoneNumber = "0455 100 000",
                customerType = CustomerType.B2C,
                modifier = Modifier
            )
        }
    }
}