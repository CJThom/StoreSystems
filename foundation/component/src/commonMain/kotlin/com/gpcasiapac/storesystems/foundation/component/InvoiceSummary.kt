package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle


@Composable
fun InvoiceSummary(
    invoices: List<String>
) {
    if (invoices.size == 1) {
        SingleInvoiceSummary(invoiceNumber = invoices.first())
    } else {
        MultipleInvoiceSummary(invoices = invoices)
    }
}


@Composable
fun InvoiceSummaryCustomerRow(customerName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Customer",
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.weight(0.35f)
        )
        Text(
            text = customerName,
            style = MaterialTheme.typography.bodyLarge,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            maxLines = 1,
            modifier = Modifier.weight(0.75f)
        )
    }
}


@Composable
fun SingleInvoiceSummary(
    invoiceNumber: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Invoice",
            style = MaterialTheme.typography.titleMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.weight(0.35f)
        )
        Text(
            text = invoiceNumber,
            style = MaterialTheme.typography.titleMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.75f)
        )
    }
}


@Composable
fun MultipleInvoiceSummary(
    invoices: List<String>,
) {
    val (invoicePreview, remaining) = buildInvoicesPreview(invoices)

    val invoiceAnnotated = buildAnnotatedString {
        withStyle(style = MaterialTheme.typography.titleMedium.toSpanStyle()) {
            append("Invoices: ")
        }
        append(invoicePreview)
        if (remaining > 0) {
            append(" +$remaining more")
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = invoiceAnnotated,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.85f)
        )
        Text(
            text = "x${invoices.size}",
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.15f)
        )
    }
}


// Build a concise invoice list preview that tends to fit within ~2 lines,
// without hardcoding heights. We use a simple character budget heuristic
// and then cap the text to maxLines = 2 with ellipsis.
private fun buildInvoicesPreview(all: List<String>, charBudget: Int = 120): Pair<String, Int> {
    if (all.isEmpty()) return "" to 0
    val sb = StringBuilder()
    var count = 0
    for (inv in all) {
        val part = if (sb.isEmpty()) inv else ", $inv"
        if (sb.length + part.length > charBudget) break
        sb.append(part)
        count++
    }
    val remaining = all.size - count
    return sb.toString() to remaining
}