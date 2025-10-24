package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
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
    contentPadding: PaddingValues = PaddingValues(),
    shape: Shape = MaterialTheme.shapes.medium,
    border: BorderStroke = BorderStroke(
        1.dp,
        MaterialTheme.colorScheme.outlineVariant
    ),
    background: Color = MaterialTheme.colorScheme.primaryContainer,
    strokeWidth: Dp = 2.dp,
    strokeColor: Color = MaterialTheme.colorScheme.onSurface,
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
    Box(modifier = modifier.padding(contentPadding)) {
        DrawCanvas(
            modifier = Modifier.fillMaxSize(),
            strokes = strokes,
            onStrokesChange = onStrokesChange,
            contentPadding = PaddingValues(),
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

        ClearButtonRow(
            visible = showClearButton && strokes.isNotEmpty(),
            enabled = strokes.isNotEmpty(),
            onClick = { onClearClick?.invoke() ?: onStrokesChange(emptyList()) }
        )

        if (showHintWhenSigned || strokes.isEmpty()) {
            OverlayContent(
                linePositionFraction = linePositionFraction,
                lineHorizontalPadding = lineHorizontalPadding,
                lineColor = lineColor,
                lineThickness = lineThickness,
                hintText = hintText,
                hintStyle = hintStyle,
                asteriskColor = asteriskColor,
                customerName = customerName
            )
        }
    }
}

@Preview(
    name = "SignHereCanvas • Scenarios",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5L,
    widthDp = 360
)
@Composable
private fun SignHereCanvasPreview(
    @PreviewParameter(SignHereCanvasPreviewProvider::class) state: SignHereCanvasPreviewState
) {
    GPCTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            SignHereCanvas(
                strokes = state.strokes,
                onStrokesChange = {},
                modifier = Modifier
                    .height(220.dp)
                    .fillMaxWidth(),
                showHintWhenSigned = state.showHintWhenSigned,
                customerName = state.customerName,
                contentPadding = PaddingValues(Dimens.Space.medium)
            )
        }
    }
}


// --- Internal subcomponents for SignHereCanvas ---
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ClearButtonRow(
    visible: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    if (!visible) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.Space.medium),
        horizontalArrangement = Arrangement.End
    ) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.height(ButtonDefaults.ExtraSmallContainerHeight)
        ) {
            Text(
                text = "CLEAR",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun OverlayContent(
    linePositionFraction: Float,
    lineHorizontalPadding: Dp,
    lineColor: Color,
    lineThickness: Dp,
    hintText: String,
    hintStyle: TextStyle,
    asteriskColor: Color,
    customerName: String,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val topWeight = linePositionFraction.coerceIn(0f, 1f)
        val bottomWeight = 1f - topWeight
        Spacer(modifier = Modifier.weight(topWeight))

        PenIconRow(lineHorizontalPadding = lineHorizontalPadding)
        SignatureBaseline(
            lineHorizontalPadding = lineHorizontalPadding,
            lineColor = lineColor,
            lineThickness = lineThickness
        )
        HintRow(
            hintText = hintText,
            hintStyle = hintStyle,
            asteriskColor = asteriskColor,
            customerName = customerName,
            lineHorizontalPadding = lineHorizontalPadding
        )

        Spacer(modifier = Modifier.weight(bottomWeight))
    }
}

@Composable
private fun PenIconRow(
    lineHorizontalPadding: Dp,
) {
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
}

@Composable
private fun SignatureBaseline(
    lineHorizontalPadding: Dp,
    lineColor: Color,
    lineThickness: Dp,
) {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = lineHorizontalPadding)
            .height(lineThickness),
        color = lineColor,
        thickness = lineThickness
    )
}

@Composable
private fun HintRow(
    hintText: String,
    hintStyle: TextStyle,
    asteriskColor: Color,
    customerName: String,
    lineHorizontalPadding: Dp,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = lineHorizontalPadding, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                append(hintText)
                append(" ")
                withStyle(SpanStyle(color = asteriskColor)) {
                    append("*")
                }
            },
            style = hintStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(.35f)
                .align(Alignment.CenterVertically)
        )

        Text(
            text = customerName,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier
                .weight(.75f)
                .align(Alignment.CenterVertically)
        )
    }
}
