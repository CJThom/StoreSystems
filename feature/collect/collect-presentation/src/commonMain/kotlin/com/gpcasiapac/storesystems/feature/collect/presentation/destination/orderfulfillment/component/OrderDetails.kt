package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BackHand
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gpcasiapac.storesystems.common.kotlin.extension.toLocalDateTimeShortString
import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.foundation.component.HeaderMedium
import com.gpcasiapac.storesystems.foundation.component.detailitem.DetailItemMedium
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.MBoltIcons
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant


@Composable
fun OrderDetails(
    invoiceNumber: InvoiceNumber,
    orderNumber: String,
    webOrderNumber: String?,
    createdAt: Instant,
    pickedAt: Instant,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues()
) {

    Column(modifier = modifier.padding(contentPadding)) {

        HeaderMedium(
            text = "Invoice: ${invoiceNumber.value}",
        )

        Column(
            modifier = Modifier.padding(Dimens.Space.medium),
            verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
        ) {

            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Space.medium),
            ) {

                DetailItemMedium(
                    modifier = Modifier.weight(1F),
                    label = "Sales Order Number",
                    value = orderNumber,
                    imageVector = Icons.Outlined.Receipt,
                    isLoading = isLoading
                )

                if (webOrderNumber != null) {
                    DetailItemMedium(
                        modifier = Modifier.weight(1F),
                        label = "Web Order Number",
                        value = webOrderNumber,
                        imageVector = MBoltIcons.Globe,
                        isLoading = isLoading
                    )
                }

            }

            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Space.medium),
            ) {

                DetailItemMedium(
                    modifier = Modifier.weight(1F),
                    label = "Created",
                    value = createdAt.toLocalDateTimeShortString(),
                    imageVector = MBoltIcons.CalendarAddOn,
                    isLoading = isLoading
                )

                DetailItemMedium(
                    modifier = Modifier.weight(1F),
                    label = "Picked",
                    value = pickedAt.toLocalDateTimeShortString(),
                    imageVector = Icons.Outlined.BackHand,
                    isLoading = isLoading
                )

            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun OrderDetailsPreview() {
    OrderDetails(
        invoiceNumber = InvoiceNumber("123456"),
        orderNumber = "SO123456",
        webOrderNumber = "W123456",
        createdAt = Clock.System.now() - 10.days,
        pickedAt = Clock.System.now(),
    )
}