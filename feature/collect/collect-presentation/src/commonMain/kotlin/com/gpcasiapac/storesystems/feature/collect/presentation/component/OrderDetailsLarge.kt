package com.gpcasiapac.storesystems.feature.collect.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CustomerDetails
import com.gpcasiapac.storesystems.feature.collect.presentation.components.ListSection
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component.OrderDetails
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
 * On large screens, the layout adapts to show order/customer details and the product list
 * side-by-side.
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
    val useColumns =
        !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    if (useColumns) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
        ) {
            OrderAndCustomerDetails(orderState = orderState)

            HorizontalDivider()

            ListSection(
                headline = "Product list",
                modifier = Modifier,
                isExpanded = isProductListExpanded,
                onViewMoreClick = onViewMoreClick,
            ) {
                orderState.lineItemList.take(visibleLineItemListCount).forEach { lineItem ->
                    ProductDetails(
                        description = lineItem.productDescription,
                        sku = lineItem.productNumber,
                        quantity = lineItem.quantity,
                        contentPadding = PaddingValues()
                    )
                }
            }
        }
    } else {
        Row(
            modifier = modifier,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
            ) {
                OrderAndCustomerDetails(orderState = orderState)
            }

            VerticalDivider()

            ListSection(
                headline = "Product list",
                modifier = Modifier.weight(1f),
                isExpanded = isProductListExpanded,
                onViewMoreClick = onViewMoreClick,
            ) {
                orderState.lineItemList.take(visibleLineItemListCount).forEach { lineItem ->
                    ProductDetails(
                        description = lineItem.productDescription,
                        sku = lineItem.productNumber,
                        quantity = lineItem.quantity,
                        contentPadding = PaddingValues()
                    )
                }
            }
        }
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


///**
// * A composable that displays the details of a single order, including order details,
// * customer details, and a list of products. This component is designed to be reusable
// * and configurable for different screen requirements.
// *
// * @param orderState The state object containing all necessary order information.
// * @param modifier The modifier to be applied to the root Column of the component.
// * @param visibleLineItemListCount The number of line items to display from the product list.
// *   Defaults to showing all items in the list.
// * @param isProductListExpanded A boolean indicating whether the product list is in an
// *   expanded state. This is used by the underlying [ListSection] to potentially change its UI.
// * @param onViewMoreClick An optional lambda that is invoked when the user requests to see
// *   more or fewer items in the product list. If null, the expand/collapse functionality
// *   is disabled, and the button will not be shown.
// */
//@Composable
//fun SingleOrderContent(
//    orderState: CollectOrderWithCustomerWithLineItemsState,
//    modifier: Modifier = Modifier,
//    visibleLineItemListCount: Int = orderState.lineItemList.size,
//    isProductListExpanded: Boolean = true,
//    onViewMoreClick: (() -> Unit)? = null,
//) {
//    LazyVerticalGrid(
//        columns = GridCells.Adaptive(minSize = 340.dp),
//        modifier = modifier,
//        verticalArrangement = Arrangement.spacedBy(Dimens.Space.small),
//        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
//    ) {
//        item {
//            OrderDetails(
//                invoiceNumber = orderState.order.invoiceNumber,
//                webOrderNumber = orderState.order.webOrderNumber,
//                createdAt = orderState.order.pickedAt, // TODO: get Order date
//                pickedAt = orderState.order.pickedAt,
//            )
//        }
//
//        item {
//            CustomerDetails(
//                customerName = orderState.customer.name,
//                customerNumber = orderState.customer.customerNumber,
//                phoneNumber = orderState.customer.mobileNumber,
//                customerType = orderState.customer.type,
//                modifier = Modifier
//            )
//        }
//
//        item(span = { GridItemSpan(maxLineSpan) }) {
//            HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.Space.small))
//        }
//
//        item(span = { GridItemSpan(maxLineSpan) }) {
//            ListSection(
//                headline = "Product list",
//                modifier = Modifier,
//                isExpanded = isProductListExpanded,
//                onViewMoreClick = onViewMoreClick,
//            ) {
//                orderState.lineItemList.take(visibleLineItemListCount).forEach { lineItem ->
//                    ProductDetails(
//                        description = lineItem.productDescription,
//                        sku = lineItem.productNumber,
//                        quantity = lineItem.quantity,
//                        contentPadding = PaddingValues()
//                    )
//                }
//            }
//        }
//    }
//}

@Preview
@Composable
private fun OrderDetailsLargePreview() {
    GPCTheme {
        Surface {
            OrderDetailsLarge(
                orderState = sampleCollectOrderWithCustomerWithLineItemsState()
            )
        }
    }
}
