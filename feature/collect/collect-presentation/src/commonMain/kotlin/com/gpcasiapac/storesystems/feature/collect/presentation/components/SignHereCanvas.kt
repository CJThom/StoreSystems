package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

/**
 * A signature pad component that composes [DrawCanvas] and overlays a helpful guideline
 * (a baseline and hints) inside the drawable area.
 *
 * The overlay is non-interactive and will not consume pointer events, so drawing remains
 * fully handled by [DrawCanvas]. By default, the hint is only shown until the user starts
 * drawing (i.e., when [strokes] is empty).
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SignHereCanvas(
    strokes: List<List<Offset>>,
    onStrokesChange: (List<List<Offset>>) -> Unit,
    modifier: Modifier = Modifier,
    // Visuals for the underlying drawing surface
    contentPadding: PaddingValues = PaddingValues(horizontal = Dimens.Space.medium),
    shape: Shape = MaterialTheme.shapes.medium,
    border: androidx.compose.foundation.BorderStroke = androidx.compose.foundation.BorderStroke(
        1.dp,
        MaterialTheme.colorScheme.outlineVariant
    ),
    background: Color = MaterialTheme.colorScheme.primaryContainer,
    strokeWidth: Dp = 2.dp,
    strokeColor: Color = Color.Black,
    // Export and completion
    exportWidth: Dp? = null,
    exportHeight: Dp? = null,
    onComplete: ((ImageBitmap) -> Unit)? = null,
    completionDelayMs: Long = 1200L,
    // Hint and guideline customization
    hintText: String = "Sign here",
    hintStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
    ),
    customerName: String = "",
    asteriskColor: Color = MaterialTheme.colorScheme.error,
    lineColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    lineThickness: Dp = 1.dp,
    lineHorizontalPadding: Dp = Dimens.Space.large,
    linePositionFraction: Float = 0.9f, // 80% down from the top by default
    showHintWhenSigned: Boolean = true,
    showClearButton: Boolean = true,
    onClearClick: (() -> Unit)? = null,
) {
    // We overlay the guideline and hint on top of DrawCanvas. Since the overlay is not clickable
    // and has no pointer input modifiers, pointer events fall through to DrawCanvas below.
    Box(modifier = modifier) {
        DrawCanvas(
            modifier = Modifier.fillMaxSize(),
            strokes = strokes,
            onStrokesChange = onStrokesChange,
            contentPadding = contentPadding,
            shape = shape,
            border = border,
            background = background,
            strokeWidth = strokeWidth,
            strokeColor = strokeColor,
            exportWidth = exportWidth,
            exportHeight = exportHeight,
            onComplete = onComplete,
            completionDelayMs = completionDelayMs
        )

        if (showClearButton && strokes.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
                    .padding(Dimens.Space.medium),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = { onClearClick?.invoke() ?: onStrokesChange(emptyList()) },
                    enabled = strokes.isNotEmpty(),
                    modifier = Modifier.height(ButtonDefaults.ExtraSmallContainerHeight)
                ) {
                    Text(
                        text = "CLEAR",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        val shouldShowHint = showHintWhenSigned || strokes.isEmpty()
        if (shouldShowHint) {
            // Draw non-interactive overlay placed inside the same padded area to match the canvas.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                // Spacer to push content to the chosen line position
                val topWeight = linePositionFraction.coerceIn(0f, 1f)
                val bottomWeight = 1f - topWeight
                Spacer(modifier = Modifier.weight(topWeight))

                // Pen icon above the baseline, aligned to the left
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = lineHorizontalPadding)
                        .padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Pen",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // The baseline users can sign on with extra horizontal padding
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = lineHorizontalPadding)
                        .height(lineThickness),
                    color = lineColor,
                    thickness = lineThickness
                )

                // Hints below the line: left = "Sign here*", right = customer name
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = lineHorizontalPadding, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left hint with red asterisk
                    Text(
                        text = buildAnnotatedString {
                            append(hintText)
                            append(" ")
                            withStyle(SpanStyle(color = asteriskColor)) {
                                append("*")
                            }
                        },
                        style = hintStyle,
                        modifier = Modifier.weight(1f)
                    )

                    // Right-aligned customer name
                    Text(
                        text = customerName,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }

                Spacer(modifier = Modifier.weight(bottomWeight))
            }
        }
    }
}

@Preview(name = "SignHereCanvas • Empty")
@Composable
private fun SignHereCanvasPreviewEmpty() {
    GPCTheme {
        SignHereCanvas(
            strokes = emptyList(),
            onStrokesChange = {},
            modifier = Modifier
                .height(220.dp)
                .fillMaxWidth(),
            customerName = "John Appleseed"
        )
    }
}

@Preview(name = "SignHereCanvas • With Strokes")
@Composable
private fun SignHereCanvasPreviewWithStrokes() {
    GPCTheme {
        SignHereCanvas(
            strokes = listOf(
                listOf(
                    Offset(10f, 10f), Offset(30f, 40f), Offset(60f, 20f), Offset(90f, 50f)
                )
            ),
            onStrokesChange = {},
            modifier = Modifier
                .height(220.dp)
                .fillMaxWidth(),
            showHintWhenSigned = false,
            customerName = "Alice Wonderland"
        )
    }
}

@Preview(name = "SignHereCanvas • With Strokes")
@Composable
private fun SignHereCanvasPreviewWithStrokesAndHint() {
    GPCTheme {
        SignHereCanvas(
            strokes = listOf(
                listOf(
                    Offset(10f, 10f), Offset(30f, 40f), Offset(60f, 20f), Offset(90f, 50f)
                )
            ),
            onStrokesChange = {},
            modifier = Modifier
                .height(220.dp)
                .fillMaxWidth(),
            showHintWhenSigned = true,
            customerName = "Alice Wonderland"
        )
    }
}