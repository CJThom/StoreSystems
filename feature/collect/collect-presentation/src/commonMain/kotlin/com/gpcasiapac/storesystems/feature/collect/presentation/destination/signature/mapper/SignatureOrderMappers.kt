package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.mapper

import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model.CollectOrderWithCustomerWithLineItemsState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureLineItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureOrderState

/**
 * Map a CollectOrderWithCustomerWithLineItemsState (presentation model used elsewhere)
 * into the light-weight SignatureOrderState used only for signature screens.
 */
fun CollectOrderWithCustomerWithLineItemsState.toSignatureOrderState(): SignatureOrderState =
    SignatureOrderState(
        invoiceNumber = order.invoiceNumber,
        customerName = customer.name,
        lineItems = lineItemList.map { line ->
            SignatureLineItemState(
                productDescription = line.productDescription,
                quantity = line.quantity
            )
        }
    )

fun List<CollectOrderWithCustomerWithLineItemsState>.toSignatureOrderStateList(): List<SignatureOrderState> =
    map { it.toSignatureOrderState() }
