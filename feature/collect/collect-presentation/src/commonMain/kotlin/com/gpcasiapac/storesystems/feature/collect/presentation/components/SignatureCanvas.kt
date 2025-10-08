package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

private fun createSmoothPath(points: List<Offset>): Path {
    val path = Path()
    if (points.isEmpty()) return path

    if (points.size == 1) {
        path.addOval(Rect(points[0], 2f))
        return path
    }

    path.moveTo(points[0].x, points[0].y)

    if (points.size == 2) {
        // Simple line for two points
        path.lineTo(points[1].x, points[1].y)
    } else {
        // Bézier curves for smooth drawing
        for (i in 1 until points.size) {
            val previous = if (i > 1) points[i - 2] else points[0]
            val current = points[i - 1]
            val next = points[i]
            val following = if (i < points.size - 1) points[i + 1] else points[i]

            // Calculate control points for smooth curves
            val cp1 = Offset(
                current.x + (next.x - previous.x) * 0.15f,
                current.y + (next.y - previous.y) * 0.15f
            )
            val cp2 = Offset(
                next.x - (following.x - current.x) * 0.15f,
                next.y - (following.y - current.y) * 0.15f
            )

            path.cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, next.x, next.y)
        }
    }

    return path
}

@Composable
fun SignatureCanvas(
    modifier: Modifier = Modifier,
    strokes: List<List<Offset>>,
    onStrokesChange: (List<List<Offset>>) -> Unit,
    strokeWidth: Dp,
    strokeColor: Color,
) {
    val density = LocalDensity.current
    val strokePx = with(density) { strokeWidth.toPx() }
    var isDrawing by remember { mutableStateOf(false) }

    val scrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // Allow parent scrolling when not actively drawing
                return if (isDrawing) {
                    // Consume all scroll events during drawing
                    available
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                return Offset.Zero
            }
        }
    }

    // Local mutable copy to allow incremental updates then publish via callback
    // Remove dependency on strokes to prevent state resets during drawing
    var localStrokes by remember { mutableStateOf(strokes) }

    // Sync with external strokes only when they change from outside (not during active drawing)
    if (localStrokes != strokes && strokes.isNotEmpty() && localStrokes.isEmpty()) {
        localStrokes = strokes
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollConnection)
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(pass = PointerEventPass.Initial)
                    isDrawing = true

                    // Constrain initial point to canvas bounds
                    val constrainedDownPosition = Offset(
                        down.position.x.coerceIn(0f, size.width.toFloat()),
                        down.position.y.coerceIn(0f, size.height.toFloat())
                    )

                    localStrokes = localStrokes + listOf(listOf(constrainedDownPosition))
                    onStrokesChange(localStrokes)

                    do {
                        val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                        var hasActivePointer = false

                        event.changes.forEach { change ->
                            if (change.id == down.id && change.pressed) {
                                change.consume()

                                // Constrain drawing point to canvas bounds
                                val constrainedPosition = Offset(
                                    change.position.x.coerceIn(0f, size.width.toFloat()),
                                    change.position.y.coerceIn(0f, size.height.toFloat())
                                )

                                val lastPath = localStrokes.lastOrNull() ?: emptyList()
                                val updatedLast = lastPath + constrainedPosition
                                localStrokes = localStrokes.dropLast(1) + listOf(updatedLast)
                                onStrokesChange(localStrokes)
                                hasActivePointer = true
                            }
                        }
                    } while (hasActivePointer)

                    isDrawing = false
                }
            }
    ) {
        localStrokes.forEach { points ->
            if (points.isNotEmpty()) {
                val path = createSmoothPath(points)
                drawPath(
                    path = path,
                    brush = SolidColor(strokeColor),
                    style = Stroke(width = strokePx)
                )
            }
        }
    }
}

@Preview
@Composable
fun SignatureCanvasPreview() {
    var strokes by remember { mutableStateOf<List<List<Offset>>>(emptyList()) }
    GPCTheme {
        Surface {
            SignatureCanvas(
                strokes = strokes,
                onStrokesChange = { strokes = it },
                strokeWidth = Dimens.Stroke.thin,
                strokeColor = Color.Black,
            )
        }
    }
}