package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.component

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureLineItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureOrderState

/**
 * Preview provider for SignatureOrderSummary that supplies a variety of scenarios:
 * - Single order with 1, 3, and 8 products
 * - Multiple orders: 2, 3, 5, and 10 orders with varying product counts
 */
class SignatureOrderSummaryOrdersProvider : PreviewParameterProvider<List<SignatureOrderState>> {
    override val values: Sequence<List<SignatureOrderState>>
        get() {
            // Single-order scenarios
            val single1 = listOf(buildOrder("10341882855", "Johnathan Citizenship", 1))
            val single3 = listOf(buildOrder("10341882856", "Alice Smith", 3))
            val single8 = listOf(buildOrder("10341882857", "Bob Brown", 8))

            // Multi-order scenarios
            val multi2 = listOf(
                buildOrder("10341882858", "Company A", 2),
                buildOrder("10341882859", "Company B", 1),
            )

            val multi3 = listOf(
                buildOrder("10341882860", "Customer 1", 3),
                buildOrder("10341882861", "Customer 2", 4),
                buildOrder("10341882862", "Customer 3", 2),
            )

            val multi5 = List(5) { idx ->
                buildOrder("1034188290${idx}", "Customer ${idx + 1}", productCount = (idx + 1) * 2)
            }

            val multi10Small = List(10) { idx ->
                buildOrder("103418830${idx}", "Cust ${idx + 1}", productCount = (idx % 2) + 1)
            }

            // Long-text scenarios for wrapping/ellipsizing tests
            val veryLongCustomerName = "Alexandria Catherine Montgomery-Smythe & Sons International Holdings Proprietary Limited"
            val singleLongNames = listOf(
                buildLongOrder(
                    invoice = "10341889999",
                    customer = veryLongCustomerName,
                    productCount = 3
                )
            )

            val multiLongInvoices = listOf(
                buildOrder("10341890001", "Long Co A", 1),
                buildOrder("10341890002", "Long Co B", 1),
                buildOrder("10341890003", "Long Co C", 1),
                buildOrder("10341890004", "Long Co D", 1),
                buildOrder("10341890005", "Long Co E", 1),
                buildOrder("10341890006", "Long Co F", 1),
            )

            val singleLongProducts = listOf(
                buildLongOrder(
                    invoice = "10341890010",
                    customer = "Customer With Extremely Long Product Names",
                    productCount = 3
                )
            )

            val multiLongProducts = listOf(
                buildLongOrder(
                    invoice = "10341890011",
                    customer = "Customer With Long Products 1",
                    productCount = 2
                ),
                buildLongOrder(
                    invoice = "10341890012",
                    customer = "Customer With Long Products 2",
                    productCount = 2
                ),
            )

            // Large quantity scenarios (to test big numbers and total)
            val singleLargeQty = listOf(
                buildOrderLargeQty(
                    invoice = "10341888888",
                    customer = "Bulk Buyer International Pty Ltd"
                )
            )

            val multiLargeTotals = listOf(
                buildOrderLargeQty(
                    invoice = "10341888889",
                    customer = "Mega Supplies Co"
                ),
                buildOrderLargeQty(
                    invoice = "10341888890",
                    customer = "Global Warehousing AU"
                )
            )

            return sequenceOf(
                single1,
                single3,
                single8,
                multi2,
                multi3,
                multi5,
                multi10Small,
                // Long-text
                singleLongNames,
                multiLongInvoices,
                singleLongProducts,
                multiLongProducts,
                // Large quantities
                singleLargeQty,
                multiLargeTotals,
            )
        }
}

private fun buildOrder(
    invoice: String,
    customer: String,
    productCount: Int,
): SignatureOrderState {
    val items = List(productCount) { idx ->
        SignatureLineItemState(
            productDescription = "Product ${idx + 1} Description",
            quantity = (idx % 3) + 1,
        )
    }
    return SignatureOrderState(
        invoiceNumber = invoice,
        customerName = customer,
        lineItems = items,
    )
}

private fun buildLongOrder(
    invoice: String,
    customer: String,
    productCount: Int,
): SignatureOrderState {
    val longBase = "Ultra-High Performance Windshield Wiper Blade with Nano-Coating Technology, All-Weather, Vehicle Fitment: Toyota Corolla 2008-2024, Part No. 9X-24B — "
    val items = List(productCount) { idx ->
        SignatureLineItemState(
            productDescription = longBase + "Variant ${idx + 1}: Includes adapters for multiple arm types, corrosion-resistant frame, OEM-grade rubber compound for silent operation, extended warranty included.",
            quantity = (idx % 3) + 1,
        )
    }
    return SignatureOrderState(
        invoiceNumber = invoice,
        customerName = customer,
        lineItems = items,
    )
}

private fun buildOrderLargeQty(
    invoice: String,
    customer: String,
): SignatureOrderState {
    val items = listOf(
        SignatureLineItemState(
            productDescription = "Bulk Pack A — Extended Description",
            quantity = 150,
        ),
        SignatureLineItemState(
            productDescription = "Bulk Pack B — Extended Description",
            quantity = 1050,
        ),
        SignatureLineItemState(
            productDescription = "Bulk Pack C — Extended Description",
            quantity = 63823,
        ),
    )
    return SignatureOrderState(
        invoiceNumber = invoice,
        customerName = customer,
        lineItems = items,
    )
}
