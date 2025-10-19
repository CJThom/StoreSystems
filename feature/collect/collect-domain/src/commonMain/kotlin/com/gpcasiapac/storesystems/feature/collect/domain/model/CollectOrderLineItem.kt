package com.gpcasiapac.storesystems.feature.collect.domain.model

data class CollectOrderLineItem(
    val lineNumber: Int,
    val sku: String,
    val productNumber: String,
    val productDescription: String,
    val quantity: Int,
    val unitPrice: Double,
    val productImageUrl: String? = null,
)
