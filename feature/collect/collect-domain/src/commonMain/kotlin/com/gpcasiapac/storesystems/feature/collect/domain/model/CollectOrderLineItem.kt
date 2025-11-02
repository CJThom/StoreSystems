package com.gpcasiapac.storesystems.feature.collect.domain.model

import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber

data class CollectOrderLineItem(
    val lineNumber: Int,
    val invoiceNumber: InvoiceNumber,
    val sku: String,
    val barcode: String?,
    val description: String,
    val quantity: Int,
    val imageUrl: String?,
)