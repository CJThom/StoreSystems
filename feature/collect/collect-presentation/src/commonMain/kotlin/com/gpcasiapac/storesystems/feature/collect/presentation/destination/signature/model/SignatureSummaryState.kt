package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model

sealed interface SignatureSummaryState {
    val totalQuantity: Int

    data class Single(
        val invoiceNumber: String,
        val customerName: String,
        override val totalQuantity: Int
    ) : SignatureSummaryState

    data class Multi(
        val invoiceNumberList: List<String>,
        override val totalQuantity: Int,
    ) : SignatureSummaryState
}

data class ProductPreview(
    val lines: List<ProductLinePreview>,
    val remainingCount: Int,
)

data class ProductLinePreview(
    val description: String,
    val quantity: Int,
)