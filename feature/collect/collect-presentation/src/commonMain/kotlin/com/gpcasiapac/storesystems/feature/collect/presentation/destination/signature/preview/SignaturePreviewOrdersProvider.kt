package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureOrderState

/**
 * Canonical provider of signature preview scenarios.
 *
 * Contains the full set of "orders lists" used by all signature-related previews
 * (SignatureOrderSummary and SignatureScreen). This ensures both previews
 * render exactly the same scenarios and reuse the same instances.
 */
class SignaturePreviewOrdersProvider : PreviewParameterProvider<List<SignatureOrderState>> {
    override val values: Sequence<List<SignatureOrderState>>
        get() {
            // Single-order scenarios
            val single1 = listOf(SignaturePreviewBuilders.buildOrder("10341882855", "Johnathan Citizenship", 1))
            val single3 = listOf(SignaturePreviewBuilders.buildOrder("10341882856", "Alice Smith", 3))
            val single8 = listOf(SignaturePreviewBuilders.buildOrder("10341882857", "Bob Brown", 8))

            // Multi-order scenarios
            val multi2 = listOf(
                SignaturePreviewBuilders.buildOrder("10341882858", "Company A", 2),
                SignaturePreviewBuilders.buildOrder("10341882859", "Company B", 1),
            )

            val multi3 = listOf(
                SignaturePreviewBuilders.buildOrder("10341882860", "Customer 1", 3),
                SignaturePreviewBuilders.buildOrder("10341882861", "Customer 2", 4),
                SignaturePreviewBuilders.buildOrder("10341882862", "Customer 3", 2),
            )

            val multi5 = List(5) { idx ->
                SignaturePreviewBuilders.buildOrder("1034188290${idx}", "Customer ${idx + 1}", productCount = (idx + 1) * 2)
            }

            val multi10Small = List(10) { idx ->
                SignaturePreviewBuilders.buildOrder("103418830${idx}", "Cust ${idx + 1}", productCount = (idx % 2) + 1)
            }

            // Long-text scenarios for wrapping/ellipsizing tests
            val veryLongCustomerName = "Alexandria Catherine Montgomery-Smythe & Sons International Holdings Proprietary Limited"
            val singleLongNames = listOf(
                SignaturePreviewBuilders.buildLongOrder(
                    invoice = "10341889999",
                    customer = veryLongCustomerName,
                    productCount = 3
                )
            )

            val multiLongInvoices = listOf(
                SignaturePreviewBuilders.buildOrder("10341890001", "Long Co A", 1),
                SignaturePreviewBuilders.buildOrder("10341890002", "Long Co B", 1),
                SignaturePreviewBuilders.buildOrder("10341890003", "Long Co C", 1),
                SignaturePreviewBuilders.buildOrder("10341890004", "Long Co D", 1),
                SignaturePreviewBuilders.buildOrder("10341890005", "Long Co E", 1),
                SignaturePreviewBuilders.buildOrder("10341890006", "Long Co F", 1),
            )

            val singleLongProducts = listOf(
                SignaturePreviewBuilders.buildLongOrder(
                    invoice = "10341890010",
                    customer = "Customer With Extremely Long Product Names",
                    productCount = 3
                )
            )

            val multiLongProducts = listOf(
                SignaturePreviewBuilders.buildLongOrder(
                    invoice = "10341890011",
                    customer = "Customer With Long Products 1",
                    productCount = 2
                ),
                SignaturePreviewBuilders.buildLongOrder(
                    invoice = "10341890012",
                    customer = "Customer With Long Products 2",
                    productCount = 2
                ),
            )

            // Large quantity scenarios (to test big numbers and total)
            val singleLargeQty = listOf(
                SignaturePreviewBuilders.buildOrderLargeQty(
                    invoice = "10341888888",
                    customer = "Bulk Buyer International Pty Ltd"
                )
            )

            val multiLargeTotals = listOf(
                SignaturePreviewBuilders.buildOrderLargeQty(
                    invoice = "10341888889",
                    customer = "Mega Supplies Co"
                ),
                SignaturePreviewBuilders.buildOrderLargeQty(
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