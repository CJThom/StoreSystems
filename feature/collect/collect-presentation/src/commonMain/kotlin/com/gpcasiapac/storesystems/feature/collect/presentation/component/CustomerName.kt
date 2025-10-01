package com.gpcasiapac.storesystems.feature.collect.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.foundation.component.icon.B2BIcon
import com.gpcasiapac.storesystems.foundation.component.icon.B2CIcon
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

// TODO: Move to foundation/components (Depends on CustomerType)
@Composable
fun CustomerName(
    customerName: String,
    customerType: CustomerType,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.small)
) {

    Row(
        modifier = modifier.padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small),
        verticalAlignment = Alignment.CenterVertically
    ) {

        when (customerType) {
            CustomerType.B2B -> B2BIcon()
            CustomerType.B2C -> B2CIcon()
        }

        Text(
            text = customerName,
            style = MaterialTheme.typography.titleMedium, // TODO: Match Figma / Fix theme Typography
        )

    }
}

@Preview
@Composable
private fun B2BCustomerNamePreview() {
    GPCTheme {
        Surface {
            CustomerName(
                customerName = "ABC Motorsports PTY Limited",
                customerType = CustomerType.B2B
            )
        }
    }
}

@Preview
@Composable
private fun B2CCustomerNamePreview() {
    GPCTheme {
        Surface {
            CustomerName(
                customerName = "Johnathan Citizenship",
                customerType = CustomerType.B2C
            )
        }
    }
}
