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

            return sequenceOf(
                single1,
                single3,
                single8,
                multi2,
                multi3,
                multi5,
                multi10Small,
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
