package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.preview

import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureSummaryState

/**
 * Shared preview data for signature summaries. Use this in any preview provider
 * to ensure consistent scenarios across components and screens.
 */
object SignatureSummaryPreviewData {
    val summaries: List<SignatureSummaryState> = listOf(
        // Single: short invoice, small qty
        SignatureSummaryState.Single(
            invoiceNumber = "1013000001",
            customerName = "Jane Doe",
            totalQuantity = 1
        ),
        // Single: long-ish customer, big qty
        SignatureSummaryState.Single(
            invoiceNumber = "1013999999",
            customerName = "Very Long Customer Name That Might Truncate In UI",
            totalQuantity = 1234
        ),
        // Multi: a couple invoices
        SignatureSummaryState.Multi(
            invoiceNumberList = listOf("1013000001", "1013000002"),
            totalQuantity = 12
        ),
        // Multi: few invoices
        SignatureSummaryState.Multi(
            invoiceNumberList = listOf("1013000001", "1013000002", "1013000003"),
            totalQuantity = 12
        ),
        // Multi: many invoices, tests truncation/ellipsis use-cases in UI
        SignatureSummaryState.Multi(
            invoiceNumberList = listOf("1013000001", "1013000002", "1013000003", "1013000004", "1013000005", "1013000006", "1013000007", "1013000008"),
            totalQuantity = 87
        ),
        // Multi: long list to test wrapping/ellipsis
        SignatureSummaryState.Multi(
            invoiceNumberList = listOf("1013000001", "1013000002", "1013000003", "1013000004"),
            totalQuantity = 999
        )
    )
}