package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.preview

import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureLineItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureOrderState

/**
 * Reusable builders for preview data across signature-related previews.
 */
object SignaturePreviewBuilders {

    /**
     * Build a simple order with [productCount] items.
     * - Description defaults to "Product N Description".
     * - Quantity defaults to cycling 1..3.
     */
    fun buildOrder(
        invoice: InvoiceNumber,
        customer: String,
        productCount: Int,
        quantityForIndex: (index: Int) -> Int = { idx -> (idx % 3) + 1 },
        descriptionForIndex: (index: Int) -> String = { idx -> "Product ${idx + 1} Description" },
    ): SignatureOrderState {
        val items = List(productCount) { idx ->
            SignatureLineItemState(
                productDescription = descriptionForIndex(idx),
                quantity = quantityForIndex(idx),
            )
        }
        return SignatureOrderState(
            invoiceNumber = invoice,
            customerName = customer,
            lineItems = items,
        )
    }

    // Convenience overload for String invoice numbers in previews
    fun buildOrder(
        invoice: String,
        customer: String,
        productCount: Int,
        quantityForIndex: (index: Int) -> Int = { idx -> (idx % 3) + 1 },
        descriptionForIndex: (index: Int) -> String = { idx -> "Product ${idx + 1} Description" },
    ): SignatureOrderState = buildOrder(InvoiceNumber(invoice), customer, productCount, quantityForIndex, descriptionForIndex)

    /**
     * Build an order where each item has a very long product description.
     */
    fun buildLongOrder(
        invoice: InvoiceNumber,
        customer: String,
        productCount: Int,
        longBase: String = "Ultra-High Performance Windshield Wiper Blade with Nano-Coating Technology, All-Weather, Vehicle Fitment: Toyota Corolla 2008-2024, Part No. 9X-24B — ",
        quantityForIndex: (index: Int) -> Int = { idx -> (idx % 3) + 1 },
    ): SignatureOrderState {
        val items = List(productCount) { idx ->
            SignatureLineItemState(
                productDescription = longBase + "Variant ${idx + 1}: Includes adapters for multiple arm types, corrosion-resistant frame, OEM-grade rubber compound for silent operation, extended warranty included.",
                quantity = quantityForIndex(idx),
            )
        }
        return SignatureOrderState(
            invoiceNumber = invoice,
            customerName = customer,
            lineItems = items,
        )
    }

    // Convenience overload for String invoice numbers in previews
    fun buildLongOrder(
        invoice: String,
        customer: String,
        productCount: Int,
        longBase: String = "Ultra-High Performance Windshield Wiper Blade with Nano-Coating Technology, All-Weather, Vehicle Fitment: Toyota Corolla 2008-2024, Part No. 9X-24B — ",
        quantityForIndex: (index: Int) -> Int = { idx -> (idx % 3) + 1 },
    ): SignatureOrderState = buildLongOrder(InvoiceNumber(invoice), customer, productCount, longBase, quantityForIndex)

    /**
     * Build an order with explicitly provided large quantities.
     * If [quantities] is shorter than [descriptions], extra descriptions are ignored; if longer, remaining quantities use last description.
     */
    fun buildOrderLargeQty(
        invoice: InvoiceNumber,
        customer: String,
        quantities: List<Int> = listOf(150, 1_050, 63_823),
        descriptions: List<String> = listOf(
            "Bulk Pack A — Extended Description",
            "Bulk Pack B — Extended Description",
            "Bulk Pack C — Extended Description",
        ),
    ): SignatureOrderState {
        val items = quantities.mapIndexed { idx, qty ->
            val desc = descriptions.getOrElse(idx) { descriptions.lastOrNull() ?: "Bulk Pack" }
            SignatureLineItemState(
                productDescription = desc,
                quantity = qty,
            )
        }
        return SignatureOrderState(
            invoiceNumber = invoice,
            customerName = customer,
            lineItems = items,
        )
    }

    // Convenience overload for String invoice numbers in previews
    fun buildOrderLargeQty(
        invoice: String,
        customer: String,
        quantities: List<Int> = listOf(150, 1_050, 63_823),
        descriptions: List<String> = listOf(
            "Bulk Pack A — Extended Description",
            "Bulk Pack B — Extended Description",
            "Bulk Pack C — Extended Description",
        ),
    ): SignatureOrderState = buildOrderLargeQty(InvoiceNumber(invoice), customer, quantities, descriptions)
}
