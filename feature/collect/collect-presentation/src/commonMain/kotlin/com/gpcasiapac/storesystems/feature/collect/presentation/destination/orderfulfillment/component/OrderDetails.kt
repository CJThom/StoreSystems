package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BackHand
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gpcasiapac.storesystems.foundation.component.detailitem.DetailItemMedium
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.MBoltIcons
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant


@Composable
fun OrderDetails(
    invoiceNumber: String,
    webOrderNumber: String?,
    createdAt: Instant,
    pickedAt: Instant,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues()
) {

    Column(modifier = modifier.padding(contentPadding)) {

        Box(modifier = Modifier.padding(Dimens.Space.medium)) {
            Text(
                text = "Invoice: $invoiceNumber",
                style = MaterialTheme.typography.titleLarge, // TODO: Emphasise?
                maxLines = 1,
            )
        }

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
                    value = invoiceNumber,
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
                    value = createdAt.toString(), // TODO: Format
                    imageVector = MBoltIcons.CalendarAddOn,
                    isLoading = isLoading
                )

                DetailItemMedium(
                    modifier = Modifier.weight(1F),
                    label = "Picked",
                    value = pickedAt.toString(), // TODO: Format
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
        invoiceNumber = "123456",
        webOrderNumber = "W123456",
        createdAt = Clock.System.now() - 10.days,
        pickedAt = Clock.System.now(),
    )
}