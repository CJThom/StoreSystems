package com.gpcasiapac.storesystems.feature.collect.presentation.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BackHand
import androidx.compose.material.icons.outlined.BackHand
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material.icons.outlined.Web
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gpcasiapac.storesystems.common.kotlin.extension.toLocalDateTimeString
import com.gpcasiapac.storesystems.common.kotlin.extension.toTimeAgoString
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.foundation.component.detailitem.DetailItemSmall
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
    contendPadding: PaddingValues = PaddingValues(
        start = Dimens.Space.medium,
        top = Dimens.Space.medium,
        end = Dimens.Space.medium,
        bottom = Dimens.Space.small
    ),
    showAbsoluteTimeInitially: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {
        OverflowMenuIconButton(
            modifier = Modifier.size(IconButtonDefaults.extraSmallContainerSize())
        ) { dismiss ->
            DropdownMenuItem(
                text = { Text("Select all for this customer") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.SelectAll,
                        contentDescription = "Select all"
                    )
                },
                onClick = { dismiss() }
            )
        }
    }
) {

    ListItemScaffold(
        modifier = modifier,
        contentPadding = contendPadding,
        toolbar = {
            val showAbsoluteTime = remember { mutableStateOf(showAbsoluteTimeInitially) }
            AssistChip(
                modifier = Modifier,
                label = {
                    AnimatedContent(
                        targetState = showAbsoluteTime.value,
                    ) { showAbs ->
                        val text = if (showAbs) pickedAt.toLocalDateTimeString() else pickedAt.toTimeAgoString()
                        Text(text = text)
                    }
                },
                colors = AssistChipDefaults.assistChipColors(
                    leadingIconContentColor = MaterialTheme.colorScheme.tertiary
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.BackHand,
                        contentDescription = null,
                        modifier = Modifier.size(SuggestionChipDefaults.IconSize)
                    )
                },
                onClick = {
                    showAbsoluteTime.value = !showAbsoluteTime.value
                }
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                actions()
            }
        }
    ) {
        CollectOrderDetailsContent(
            customerName = customerName,
            customerType = customerType,
            invoiceNumber = invoiceNumber,
            webOrderNumber = webOrderNumber,
            isLoading = isLoading
        )
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

@Preview(name = "CollectOrderDetails Full Date", showBackground = true)
@Composable
private fun CollectOrderDetailsFullDatePreview(
    @PreviewParameter(CollectOrderDetailsPreviewProvider::class) data: CollectOrderDetailsParams
) {
    GPCTheme {
        CollectOrderDetails(
            customerName = data.customerName,
            customerType = data.customerType,
            invoiceNumber = data.invoiceNumber,
            webOrderNumber = data.webOrderNumber,
            pickedAt = data.pickedAt,
            showAbsoluteTimeInitially = true
        )
    }
}
