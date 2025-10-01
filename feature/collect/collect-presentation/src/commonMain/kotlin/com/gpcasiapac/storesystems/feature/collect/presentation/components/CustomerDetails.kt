package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.presentation.component.CustomerName
import com.gpcasiapac.storesystems.foundation.component.detailitem.DetailItemMedium
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

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
    phoneNumber: String,
    customerType: CustomerType,
    modifier: Modifier = Modifier
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)) {
        //Use BusinessNameSection for B2B.
        CustomerName(
            customerName = customerName,
            customerType = customerType,
            modifier = Modifier.padding(horizontal = Dimens.Space.medium)
        )
        // Details section with customer number and phone
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.Space.medium),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Space.large)
        ) {
            // Left column - Customer Number
            DetailItemMedium(
                imageVector = Icons.Outlined.Receipt,
                label = "Customer Number",
                value = customerNumber,
                modifier = Modifier.weight(1f)
            )

            // Right column - Phone
            DetailItemMedium(
                imageVector = Icons.Outlined.Call,
                label = "Phone",
                value = phoneNumber,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Preview for CustomerDetails in light theme
 */
@Preview
@Composable
private fun CustomerDetailsLightPreview() {
    GPCTheme {
        Surface {
            CustomerDetails(
                customerName = "Johnathan Citizenship",
                customerNumber = "1887388193",
                phoneNumber = "0455 100 000",
                customerType = CustomerType.B2B,
                modifier = Modifier.padding(Dimens.Space.medium)
            )
        }
    }
}

/**
 * Preview for CustomerDetails in dark theme
 */
@Preview
@Composable
private fun CustomerDetailsDarkPreview() {
    GPCTheme {
        Surface {
            CustomerDetails(
                customerName = "Johnathan Citizenship",
                customerNumber = "1887388193",
                phoneNumber = "0455 100 000",
                customerType = CustomerType.B2C,
                modifier = Modifier.padding(Dimens.Space.medium)
            )
        }
    }
}