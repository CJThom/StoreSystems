package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureLineItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureOrderState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.preview.SignaturePreviewBuilders
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.preview.SignaturePreviewOrdersProvider

class SignatureScreenStateProvider : PreviewParameterProvider<SignatureScreenContract.State> {
    override val values: Sequence<SignatureScreenContract.State>
        get() {
            // Reuse the exact same SignatureOrderState lists across previews
            val ordersProvider = SignaturePreviewOrdersProvider()
            val allOrdersLists: List<List<SignatureOrderState>> = ordersProvider.values.toList()

            val base = SignatureScreenContract.State(
                isLoading = false,
                isSigned = false,
                error = null,
                signatureStrokes = emptyList(),
                signatureBitmap = null,
                customerName = "John Appleseed",
                selectedOrderList = emptyList()
            )

            // Map each shared orders list into a screen state without recreating the orders
            val scenarioStates = allOrdersLists.map { orders ->
                base.copy(selectedOrderList = orders)
            }

            // Additional UI states (signed, loading, error) using the first shared orders list
            val firstOrders = allOrdersLists.firstOrNull() ?: emptyList()

            val signed = base.copy(
                selectedOrderList = firstOrders,
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

            val loading = base.copy(
                selectedOrderList = firstOrders,
                isLoading = true
            )

            val error = base.copy(
                selectedOrderList = firstOrders,
                error = "Failed to capture signature. Please try again."
            )

            return (scenarioStates + listOf(signed, loading, error)).asSequence()
        }
}


