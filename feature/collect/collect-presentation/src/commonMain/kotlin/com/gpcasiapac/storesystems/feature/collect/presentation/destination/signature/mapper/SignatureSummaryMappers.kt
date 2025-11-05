package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.mapper

import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureSummaryState

private const val DEFAULT_INVOICE_CHAR_BUDGET = 120

/**
 * Direct domain → SignatureSummaryState mapper for efficiency in ViewModel.
 */
fun List<CollectOrderWithCustomerWithLineItems>.toSignatureSummary(
    invoiceCharBudget: Int = DEFAULT_INVOICE_CHAR_BUDGET
): SignatureSummaryState {
    val safeBudget = invoiceCharBudget.coerceAtLeast(0)
    return if (size == 1) {
        val single = first()
        val totalQty = single.lineItemList.sumOf { it.quantity }
        SignatureSummaryState.Single(
            invoiceNumber = single.order.invoiceNumber.value,
            customerName = single.customer.name,
            totalQuantity = totalQty
        )
    } else {
        val allInvoices: List<InvoiceNumber> = map { it.order.invoiceNumber }
        val (joined, _) = buildInvoicesPreview(allInvoices, safeBudget)
        val totalQty = sumOf { domain -> domain.lineItemList.sumOf { it.quantity } }
        SignatureSummaryState.Multi(
            orderCount = size,
            joinedText = joined,
            totalQuantity = totalQty
        )
    }
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
        val piece = if (sb.isEmpty()) inv.value else ", ${inv.value}"
        if (sb.length + piece.length > budget) return@forEach
        sb.append(piece)
        count++
    }
    val remaining = invoices.size - count
    return sb.toString() to remaining
}
