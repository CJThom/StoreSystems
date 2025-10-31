package com.gpcasiapac.storesystems.feature.collect.domain.model

data class CollectOrderLineItem(
    val lineNumber: Int,
    val invoiceNumber: String,
    val sku: String,
    val barcode: String?,
    val description: String,
    val quantity: Int,
    val imageUrl: String?,
)