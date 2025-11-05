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
            orderCount = 2,
            joinedText = "1013000001, 1013000002",
            totalQuantity = 12
        ),
        // Multi: few invoices
        SignatureSummaryState.Multi(
            orderCount = 3,
            joinedText = "1013000001, 1013000002, 1013000003",
            totalQuantity = 12
        ),
        // Multi: many invoices, tests truncation/ellipsis use-cases in UI
        SignatureSummaryState.Multi(
            orderCount = 8,
            joinedText = "1013000001, 1013000002, 1013000003, 1013000004, 1013000005",
            totalQuantity = 87
        ),
        // Multi: extremely long joined text to test wrapping/ellipsis
        SignatureSummaryState.Multi(
            orderCount = 5,
            joinedText = "1013000001, 1013000002, 1013000003, 1013000004",
            totalQuantity = 999
        )
    )
}