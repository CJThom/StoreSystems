package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.sampleCollectOrderWithCustomerWithLineItemsState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.mapper.toSignatureOrderState

class SignatureScreenStateProvider : PreviewParameterProvider<SignatureScreenContract.State> {
    override val values: Sequence<SignatureScreenContract.State>
        get() {
            val singleOrder = sampleCollectOrderWithCustomerWithLineItemsState()
            val multiOrders = listOf(
                singleOrder,
                singleOrder.copy(order = singleOrder.order.copy(invoiceNumber = "10341882856"))
            )

            val base = SignatureScreenContract.State(
                isLoading = false,
                isSigned = false,
                error = null,
                signatureStrokes = emptyList(),
                signatureBitmap = null,
                customerName = "John Appleseed",
                selectedOrderList = listOf(singleOrder.toSignatureOrderState())
            )

            val signed = base.copy(
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

            val multi = base.copy(selectedOrderList = multiOrders.map { it.toSignatureOrderState() })

            val loading = base.copy(
                isLoading = true
            )

            val error = base.copy(
                error = "Failed to capture signature. Please try again."
            )

            return sequenceOf(
                base,
                multi,
                signed,
                loading,
                error
            )
        }
}
