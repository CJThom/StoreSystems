package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail.model


data class CollectOrderWithCustomerWithLineItemsState(
    val order: CollectOrderState,
    val customer: CollectOrderCustomerState,
    val lineItemList: List<CollectOrderLineItemState>
){

    companion object {
        fun placeholder(): CollectOrderWithCustomerWithLineItemsState {
            return CollectOrderWithCustomerWithLineItemsState(
                order = CollectOrderState.placeholder(),
                customer = CollectOrderCustomerState.placeholder(),
                lineItemList = CollectOrderLineItemState.placeholderList()
            )
        }
    }

}

