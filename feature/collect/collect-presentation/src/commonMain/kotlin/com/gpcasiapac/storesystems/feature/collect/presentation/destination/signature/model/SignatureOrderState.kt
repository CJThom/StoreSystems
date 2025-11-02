package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model

import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber

/**
 * Lightweight display models for the Signature flow. These contain only the
 * fields necessary for rendering the SignatureOrderSummary and related UI.
 */

data class SignatureOrderState(
    val invoiceNumber: InvoiceNumber,
    val customerName: String,
    val lineItems: List<SignatureLineItemState>
)

/**
 * Minimal representation of a line item for the signature summary display.
 */
data class SignatureLineItemState(
    val productDescription: String,
    val quantity: Int,
)