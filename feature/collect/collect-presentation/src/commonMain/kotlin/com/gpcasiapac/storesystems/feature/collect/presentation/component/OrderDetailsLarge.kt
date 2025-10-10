package com.gpcasiapac.storesystems.feature.collect.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CustomerDetails
import com.gpcasiapac.storesystems.feature.collect.presentation.components.ListSection
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component.OrderDetails
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model.CollectOrderWithCustomerWithLineItemsState

/**
 * A composable that displays the details of a single order, including order details,
 * customer details, and a list of products. This component is designed to be reusable
 * and configurable for different screen requirements.
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
@Composable
fun OrderDetailsLarge(
    orderState: CollectOrderWithCustomerWithLineItemsState,
    modifier: Modifier = Modifier,
    visibleLineItemListCount: Int = orderState.lineItemList.size,
    isProductListExpanded: Boolean = true,
    onViewMoreClick: (() -> Unit)? = null,
) {
    Column(modifier = modifier) {
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