package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.mapper

import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.InvoicePreview
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.ProductLinePreview
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.ProductPreview
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureOrderState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureSummaryState

private const val DEFAULT_INVOICE_CHAR_BUDGET = 120
private const val DEFAULT_PRODUCT_PREVIEW_LIMIT = 3


fun List<SignatureOrderState>.toSignatureSummary(
    invoiceCharBudget: Int = DEFAULT_INVOICE_CHAR_BUDGET,
    productPreviewLimit: Int = DEFAULT_PRODUCT_PREVIEW_LIMIT,
): SignatureSummaryState {
    val safeBudget = invoiceCharBudget.coerceAtLeast(0)
    val safeLimit = productPreviewLimit.coerceAtLeast(0)
    return if (size == 1) {
        val single = first()
        val totalQty = single.lineItems.sumOf { it.quantity }
        SignatureSummaryState.Single(
            invoiceNumber = single.invoiceNumber.value,
            customerName = single.customerName,
            totalQuantity = totalQty,
            productPreview = single.buildProductPreview(safeLimit)
        )
    } else {
        val allInvoices = map { it.invoiceNumber }
        val (joined, remaining) = buildInvoicesPreview(allInvoices, safeBudget)
        val totalQty = sumOf { order -> order.lineItems.sumOf { it.quantity } }
        SignatureSummaryState.Multi(
            orderCount = size,
            invoicePreview = InvoicePreview(joinedText = joined, remainingCount = remaining),
            totalQuantity = totalQty
        )
    }
}

private fun SignatureOrderState.buildProductPreview(limit: Int): ProductPreview {
    val safe = limit.coerceAtLeast(0)
    val previewLines = lineItems.take(safe).map {
        ProductLinePreview(description = it.productDescription, quantity = it.quantity)
    }
    val remaining = (lineItems.size - previewLines.size).coerceAtLeast(0)
    return ProductPreview(lines = previewLines, remainingCount = remaining)
}

private fun buildInvoicesPreview(
    invoices: List<InvoiceNumber>,
    charBudget: Int,
): Pair<String, Int> {
    if (invoices.isEmpty()) return "" to 0
    val budget = charBudget.coerceAtLeast(0)
    if (budget == 0) return "" to invoices.size
    val sb = StringBuilder()
    var count = 0
    invoices.forEach { inv ->
        val piece = if (sb.isEmpty()) inv.value else ", ${'$'}{inv.value}"
        if (sb.length + piece.length > budget) return@forEach
        sb.append(piece)
        count++
    }
    val remaining = invoices.size - count
    return sb.toString() to remaining
}
