package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * Preview state for SignHereCanvas. We vary only what we want to visually test:
 * - strokes presence (empty vs. some signature)
 * - customerName length (short vs. very long)
 * - whether to keep hint visible even when signed
 */
data class SignHereCanvasPreviewState(
    val strokes: List<List<Offset>>,
    val customerName: String,
    val showHintWhenSigned: Boolean = true,
)

class SignHereCanvasPreviewProvider : PreviewParameterProvider<SignHereCanvasPreviewState> {
    override val values: Sequence<SignHereCanvasPreviewState>
        get() {
            val shortName = "John Appleseed"
            val veryLongName =
                "Alexandria Catherine Montgomery-Smythe & Sons International Holdings Proprietary Limited"
            val mediumLongName = "Alexandra Catherine Montgomery Smythe"

            val empty = emptyList<List<Offset>>()
            val strokes1 = listOf(
                listOf(
                    Offset(10f, 10f), Offset(30f, 40f), Offset(60f, 20f), Offset(90f, 50f)
                )
            )
            val strokes2 = listOf(
                listOf(
                    Offset(20f, 25f), Offset(35f, 55f), Offset(70f, 35f), Offset(110f, 65f)
                ),
                listOf(
                    Offset(15f, 70f), Offset(45f, 90f), Offset(95f, 80f)
                )
            )

            return sequenceOf(
                // Empty signature, short name (baseline + left hint + short right label)
                SignHereCanvasPreviewState(empty, shortName, showHintWhenSigned = true),
                // Empty signature, very long name (test right label ellipsis)
                SignHereCanvasPreviewState(empty, veryLongName, showHintWhenSigned = true),
                // Signed (single stroke), short name, hide hint when signed
                SignHereCanvasPreviewState(strokes1, shortName, showHintWhenSigned = false),
                // Signed (multiple strokes), very long name, hide hint when signed
                SignHereCanvasPreviewState(strokes2, veryLongName, showHintWhenSigned = false),
                // Signed (multiple strokes), medium-long name, force hint to remain visible
                SignHereCanvasPreviewState(strokes2, mediumLongName, showHintWhenSigned = true),
            )
        }
}

