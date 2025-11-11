package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureSummaryState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.preview.SignatureSummaryPreviewData

class SignatureScreenStateProvider : PreviewParameterProvider<SignatureScreenContract.State> {
    override val values: Sequence<SignatureScreenContract.State>
        get() {
            val base = SignatureScreenContract.State(
                isLoading = false,
                isSigned = false,
                error = null,
                signatureStrokes = emptyList(),
                signatureBitmap = null,
                customerName = "John Appleseed",
                summary = SignatureSummaryState.Multi(
                    invoiceNumberList = emptyList(),
                    totalQuantity = 0
                )
            )

            // Map shared summaries into screen states
            val summaryStates = SignatureSummaryPreviewData.summaries.map { summary ->
                base.copy(summary = summary)
            }

            // Add additional UI states using the first shared summary
            val firstSummary = SignatureSummaryPreviewData.summaries.firstOrNull()
            val extraStates = if (firstSummary != null) listOf(
                base.copy(
                    summary = firstSummary,
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
                ),
                base.copy(
                    summary = firstSummary,
                    isLoading = true
                ),
                base.copy(
                    summary = firstSummary,
                    error = "Failed to capture signature. Please try again."
                )
            ) else emptyList()

            return (summaryStates + extraStates).asSequence()
        }
}


