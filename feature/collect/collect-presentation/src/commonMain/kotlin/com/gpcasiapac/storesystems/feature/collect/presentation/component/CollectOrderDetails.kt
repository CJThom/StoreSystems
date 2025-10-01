package com.gpcasiapac.storesystems.feature.collect.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BackHand
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Web
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gpcasiapac.storesystems.common.kotlin.extension.toTimeAgoString
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.foundation.component.detailitem.DetailItemSmall
import com.gpcasiapac.storesystems.foundation.component.detailitem.DetailItemSmallChip
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import kotlin.time.Instant

@Composable
fun CollectOrderDetails(
    customerName: String,
    customerType: CustomerType,
    invoiceNumber: String,
    webOrderNumber: String?,
    pickedAt: Instant,
    modifier: Modifier = Modifier,
    contendPadding: PaddingValues = PaddingValues(Dimens.Space.medium)
) {

    Column(
        modifier = modifier.padding(contendPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall),
    ) {

        CustomerName(
            customerName = customerName,
            customerType = customerType,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small),
            verticalAlignment = Alignment.CenterVertically
        ) {

            DetailItemSmall(
                value = invoiceNumber,
                imageVector = Icons.Outlined.Receipt,
                modifier = Modifier
            )

            if (webOrderNumber != null) {
                DetailItemSmall(
                    value = webOrderNumber,
                    imageVector = Icons.Outlined.Web, // TODO: Get globe icon
                )
            }

        }

        DetailItemSmallChip(
            value = pickedAt.toTimeAgoString(),
            imageVector = Icons.Outlined.BackHand,
            modifier = Modifier
        )

    }

}

private data class CollectOrderDetailsParams(
    val customerName: String,
    val customerType: CustomerType,
    val invoiceNumber: String,
    val webOrderNumber: String?,
    val pickedAt: Instant,
)

private class CollectOrderDetailsPreviewProvider :
    PreviewParameterProvider<CollectOrderDetailsParams> {
    override val values = sequenceOf(
        CollectOrderDetailsParams(
            customerName = "ABC Motorsports PTY Limited",
            customerType = CustomerType.B2B,
            invoiceNumber = "1234567890",
            webOrderNumber = "ABC-1234567890",
            pickedAt = Instant.parse("2025-09-29T00:00:00Z")
        ),
        CollectOrderDetailsParams(
            customerName = "Johnathan Josiah Citizenship Esq.",
            customerType = CustomerType.B2C,
            invoiceNumber = "1234567890",
            webOrderNumber = "ABC-1234567890",
            pickedAt = Instant.parse("2025-09-03T00:00:00Z")
        ),
        CollectOrderDetailsParams(
            customerName = "Short Co",
            customerType = CustomerType.B2B,
            invoiceNumber = "1234567890",
            webOrderNumber = null,
            pickedAt = Instant.parse("2025-09-20T00:00:00Z")
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun DetailItemMediumPreview(
    @PreviewParameter(CollectOrderDetailsPreviewProvider::class) data: CollectOrderDetailsParams
) {
    GPCTheme {
        CollectOrderDetails(
            customerName = data.customerName,
            customerType = data.customerType,
            invoiceNumber = data.invoiceNumber,
            webOrderNumber = data.webOrderNumber,
            pickedAt = data.pickedAt
        )
    }
}
