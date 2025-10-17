package com.gpcasiapac.storesystems.common.presentation.compose.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.MaterialTheme

/**
 * Dashed border modifiers that are shape-aware and token-friendly.
 *
 * The base overloads are non-composable and do not depend on MaterialTheme. They accept explicit
 * color/brush/width or a BorderStroke. A composable overload is provided to resolve defaults from
 * the Border tokens exposed via MaterialTheme (see Border.kt).
 */

private fun Modifier.dashedBorderInternal(
    brush: Brush,
    width: Dp,
    intervals: FloatArray,
    shape: Shape,
): Modifier = this.drawBehind {
    val stroke = Stroke(
        width = width.toPx(),
        pathEffect = PathEffect.dashPathEffect(intervals, 0f)
    )
    when (val outline = shape.createOutline(size, layoutDirection, this)) {
        is Outline.Rounded -> {
            val path = Path().apply { addRoundRect(outline.roundRect) }
            drawPath(path = path, brush = brush, alpha = 1f, style = stroke)
        }
        is Outline.Generic -> {
            drawPath(path = outline.path, brush = brush, alpha = 1f, style = stroke)
        }
        is Outline.Rectangle -> {
            // Fast path for rectangles
            drawRect(brush = brush, style = stroke)
        }
    }
}

/** Non-composable overload using a Brush and an explicit width. */
fun Modifier.dashedBorder(
    brush: Brush,
    width: Dp,
    intervals: FloatArray = floatArrayOf(12f, 8f),
    shape: Shape = RectangleShape,
): Modifier = dashedBorderInternal(brush, width, intervals, shape)

/** Non-composable overload using a Color and an explicit width. */
fun Modifier.dashedBorder(
    color: Color,
    width: Dp,
    intervals: FloatArray = floatArrayOf(12f, 8f),
    shape: Shape = RectangleShape,
): Modifier = dashedBorderInternal(SolidColor(color), width, intervals, shape)

/** Non-composable overload using a BorderStroke (token-agnostic). */
fun Modifier.dashedBorder(
    border: BorderStroke,
    intervals: FloatArray = floatArrayOf(12f, 8f),
    shape: Shape = RectangleShape,
): Modifier = dashedBorderInternal(border.brush, border.width, intervals, shape)

/**
 * Composable convenience that pulls defaults from MaterialTheme border tokens.
 *
 * - role chooses the semantic color via MaterialTheme.borderColors
 * - size (nullable) resolves to MaterialTheme.componentBorders.defaultSize when null
 */
@Composable
fun Modifier.dashedBorder(
    role: BorderRole = BorderRole.Variant,
    size: BorderSize? = null,
    intervals: FloatArray = floatArrayOf(12f, 8f),
    shape: Shape = RectangleShape,
): Modifier {
    val stroke = MaterialTheme.borderStroke(role = role, size = size)
    return dashedBorder(stroke, intervals, shape)
}
