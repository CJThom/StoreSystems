package com.gpcasiapac.storesystems.feature.collect.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CustomerDetails
import com.gpcasiapac.storesystems.feature.collect.presentation.components.HeaderMedium
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component.OrderDetails
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model.CollectOrderLineItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model.CollectOrderWithCustomerWithLineItemsState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.sampleCollectOrderWithCustomerWithLineItemsState
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A composable that displays the details of a single order, including order details,
 * customer details, and a list of products. This component is designed to be reusable
 * and configurable for different screen requirements.
 *
 * On large screens, the product list adapts to show items in a grid.
 *
 * @param orderState The state object containing all necessary order information.
 * @param modifier The modifier to be applied to the root Column of the component.
 * @param visibleLineItemListCount The number of line items to display from the product list.
 *   Defaults to showing all items in the list.
 * @param isProductListExpanded A boolean indicating whether the product list is in an
 *   expanded state. This is used by the underlying [com.gpcasiapac.storesystems.feature.collect.presentation.components.ListSection] to potentially change its UI.
 * @param onViewMoreClick An optional lambda that is invoked when the user requests to see
 *   more or fewer items in the product list. If null, the expand/collapse functionality
 *   is disabled, and the button will not be shown.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun OrderDetailsLarge(
    orderState: CollectOrderWithCustomerWithLineItemsState,
    modifier: Modifier = Modifier,
    visibleLineItemListCount: Int = orderState.lineItemList.size,
    isProductListExpanded: Boolean = true,
    onViewMoreClick: (() -> Unit)? = null,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val useGridForProducts =
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
    ) {
        OrderAndCustomerDetails(orderState = orderState)

        HorizontalDivider()

        ProductListSection(
            modifier = Modifier,
            lineItemList = orderState.lineItemList.take(visibleLineItemListCount),
            isProductListExpanded = isProductListExpanded,
            onViewMoreClick = onViewMoreClick,
            useGrid = useGridForProducts
        )
    }
}

@Composable
private fun OrderAndCustomerDetails(orderState: CollectOrderWithCustomerWithLineItemsState) {
    OrderDetails(
        invoiceNumber = orderState.order.invoiceNumber,
        webOrderNumber = orderState.order.webOrderNumber,
        createdAt = orderState.order.pickedAt, // TODO: get Order date
        pickedAt = orderState.order.pickedAt,
    )
    HorizontalDivider()
    CustomerDetails(
        customerName = orderState.customer.name,
        customerNumber = orderState.customer.customerNumber,
        phoneNumber = orderState.customer.mobileNumber,
        customerType = orderState.customer.type,
        modifier = Modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProductListSection(
    lineItemList: List<CollectOrderLineItemState>,
    isProductListExpanded: Boolean,
    onViewMoreClick: (() -> Unit)?,
    useGrid: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(Dimens.Space.medium),
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
    ) {
        HeaderMedium(
            text = "Product list",
            contentPadding = PaddingValues()
        )

        if (useGrid) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = 2,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Space.medium),
                verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
            ) {
                lineItemList.forEach { lineItem ->
                    ProductDetails(
                        modifier = Modifier
                            .weight(1f),
                        description = lineItem.productDescription,
                        sku = lineItem.productNumber,
                        quantity = lineItem.quantity,
                        contentPadding = PaddingValues()
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)) {
                lineItemList.forEach { lineItem ->
                    ProductDetails(
                        description = lineItem.productDescription,
                        sku = lineItem.productNumber,
                        quantity = lineItem.quantity,
                        contentPadding = PaddingValues()
                    )
                }
            }
        }

        if (onViewMoreClick != null) {
            OutlinedButton(
                onClick = onViewMoreClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = if (isProductListExpanded) "VIEW LESS" else "VIEW MORE",
                )
            }
        }
    }
}


@Preview
@Composable
private fun OrderDetailsLargePreview() {
    GPCTheme {
        Surface {
            OrderDetailsLarge(
                orderState = sampleCollectOrderWithCustomerWithLineItemsState(),
                onViewMoreClick = {}
            )
        }
    }
}
