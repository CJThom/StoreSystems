package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureLineItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureOrderState

class SignatureScreenStateProvider : PreviewParameterProvider<SignatureScreenContract.State> {
    override val values: Sequence<SignatureScreenContract.State>
        get() {
            // Single-order scenarios with varying product counts
            val single1 = listOf(buildOrder("10341882855", "Johnathan Citizenship", 1))
            val single3 = listOf(buildOrder("10341882856", "Alice Smith", 3))
            val single8 = listOf(buildOrder("10341882857", "Bob Brown", 8))

            // Multi-order scenarios mirroring SignatureOrderSummary previews
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
                buildOrder("1034188290$idx", "Customer ${idx + 1}", (idx + 1) * 2)
            }
            val multi10Small = List(10) { idx ->
                buildOrder("103418830$idx", "Cust ${idx + 1}", (idx % 2) + 1)
            }

            val base = SignatureScreenContract.State(
                isLoading = false,
                isSigned = false,
                error = null,
                signatureStrokes = emptyList(),
                signatureBitmap = null,
                customerName = "John Appleseed",
                selectedOrderList = single1
            )

            val single3State = base.copy(selectedOrderList = single3)
            val single8State = base.copy(selectedOrderList = single8)
            val multi2State = base.copy(selectedOrderList = multi2)
            val multi3State = base.copy(selectedOrderList = multi3)
            val multi5State = base.copy(selectedOrderList = multi5)
            val multi10SmallState = base.copy(selectedOrderList = multi10Small)

            val signed = single3State.copy(
                isSigned = true,
                signatureStrokes = listOf(
                    listOf(
                        Offset(10f, 10f),
                        Offset(30f, 40f),
                        Offset(60f, 20f),
                        Offset(90f, 50f)
                    ),
                    listOf(
                        Offset(100f, 60f),
                        Offset(120f, 80f),
                        Offset(140f, 65f)
                    )
                )
            )

            val loading = multi3State.copy(
                isLoading = true
            )

            val error = base.copy(
                error = "Failed to capture signature. Please try again."
            )

            return sequenceOf(
                base,
                single3State,
                single8State,
                multi2State,
                multi3State,
                multi5State,
                multi10SmallState,
                signed,
                loading,
                error,
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
