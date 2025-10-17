package com.gpcasiapac.storesystems.feature.collect.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BackHand
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Propane
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Web
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CollectOrderDetails(
    customerName: String,
    customerType: CustomerType,
    invoiceNumber: String,
    webOrderNumber: String?,
    pickedAt: Instant,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    contendPadding: PaddingValues = PaddingValues( Dimens.Space.medium)
) {

    // Overflow menu on the right
    var showMenu = remember { mutableStateOf(false) }

    Column(
        modifier = modifier.height(IntrinsicSize.Min).padding(contendPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall),
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
                    imageVector = Icons.Outlined.Web, // TODO: Get globe icon
                    isLoading = isLoading,
                    modifier = Modifier
                )
            }

        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DetailItemSmallChip(
                value = pickedAt.toTimeAgoString(),
                imageVector = Icons.Outlined.BackHand,
                modifier = Modifier,
                isLoading = isLoading
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Primary small action button(s)
                IconButton(
                    onClick = { /*TODO*/ },
                    colors = IconButtonDefaults.iconButtonColors(),
                    modifier = Modifier.size(IconButtonDefaults.extraSmallContainerSize())
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Propane"
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMenu.value = true },
                        modifier = Modifier.size(IconButtonDefaults.extraSmallContainerSize())
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = "More"
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu.value,
                        onDismissRequest = { showMenu.value = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Select all for this customer") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.SelectAll,
                                    contentDescription = "Select all"
                                )
                            },
                            onClick = {
                                showMenu.value = false
                                // TODO: handle delete click
                            }
                        )
                    }
                }
            }
        }
    }
}

//                clickableItem(
//                    label = "Delete",
//                    onClick = {
//
//                    },
//                    icon = {
//                        Icon(
//                            imageVector = Icons.Outlined.Delete,
//                            contentDescription = "Delete",
//                            modifier = Modifier.size(12.dp)
//                        )
//                    }
//                )


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
