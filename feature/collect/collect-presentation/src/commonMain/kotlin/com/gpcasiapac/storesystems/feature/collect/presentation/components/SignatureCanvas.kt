package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SignatureCanvas(
    modifier: Modifier = Modifier,
    strokes: List<List<Offset>>,
    onStrokesChange: (List<List<Offset>>) -> Unit,
    shape: Shape = MaterialTheme.shapes.medium,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    background: Color = MaterialTheme.colorScheme.primaryContainer,
    strokeWidth: Dp = 3.dp,
    strokeColor: Color,
    exportWidth: Dp? = null,
    exportHeight: Dp? = null,
    onComplete: ((ImageBitmap?) -> Unit)? = null,
    completionDelayMs: Long = 2000L, // 2 seconds of inactivity
) {
    val density = LocalDensity.current
    val strokePx = with(density) { strokeWidth.toPx() }
    var isDrawing by remember { mutableStateOf(false) }
    var lastDrawTime by remember { mutableLongStateOf(0L) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

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

    LaunchedEffect(strokes) {
        // Sync with external strokes only when they change from outside (not during active drawing)
        if (localStrokes != strokes && strokes.isNotEmpty() && localStrokes.isEmpty() && !isDrawing) {
            localStrokes = strokes
        }
    }

    // Auto-completion effect
    LaunchedEffect(lastDrawTime, localStrokes) {
        if (onComplete != null && lastDrawTime > 0L && localStrokes.isNotEmpty()) {
            delay(completionDelayMs)

            // Check if no new drawing happened during the delay
            if (System.currentTimeMillis() - lastDrawTime >= completionDelayMs) {
                val exportWidthPx = exportWidth?.let { with(density) { it.toPx().toInt() } }
                    ?: canvasSize.width
                val exportHeightPx = exportHeight?.let { with(density) { it.toPx().toInt() } }
                    ?: canvasSize.height

                if (exportWidthPx > 0 && exportHeightPx > 0) {
                    val bitmap = createSignatureBitmap(
                        strokes = localStrokes,
                        width = exportWidthPx,
                        height = exportHeightPx,
                        strokeWidth = strokePx,
                        strokeColor = strokeColor,
                        density = density
                    )
                    onComplete(bitmap)
                }
            }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollConnection)
            .background(background)
            .border(border, shape)
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(pass = PointerEventPass.Initial)
                    isDrawing = true
                    lastDrawTime = System.currentTimeMillis()

                    val boundSize = calculateDrawingSafeArea(
                        borderWidthPx = with(density) { border.width.roundToPx() },
                        actualSize = size,
                    )
                    // Update canvas size
                    canvasSize = boundSize.size
                    val newOffset = boundSize.offset

                    // Constrain initial point to canvas bounds
                    val constrainedDownPosition = Offset(
                        down.position.x.coerceIn(newOffset.x, boundSize.size.width.toFloat()),
                        down.position.y.coerceIn(newOffset.y, boundSize.size.height.toFloat())
                    )

                    localStrokes = localStrokes + listOf(listOf(constrainedDownPosition))
                    onStrokesChange(localStrokes)

                    do {
                        val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                        var hasActivePointer = false

                        event.changes.forEach { change ->
                            if (change.id == down.id && change.pressed) {
                                change.consume()
                                lastDrawTime = System.currentTimeMillis()

                                // Constrain drawing point to canvas bounds
                                val constrainedPosition = Offset(
                                    change.position.x.coerceIn(newOffset.x, boundSize.size.width.toFloat()),
                                    change.position.y.coerceIn(newOffset.y, boundSize.size.height.toFloat())
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
            },
    ) {
        // Update canvas size for bitmap export
        val boundSize = calculateDrawingSafeArea(
            borderWidthPx = with(density) { border.width.roundToPx() },
            actualSize = IntSize(size.width.toInt(), size.height.toInt()),
        )
        canvasSize = boundSize.size

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


private data class DrawingSafeArea(
    val size: IntSize,
    val offset: Offset = Offset.Zero,
)

private fun calculateDrawingSafeArea(
    borderWidthPx: Int,
    actualSize: IntSize,
    bufferPx: Int = 8
): DrawingSafeArea {
    val reduction = (borderWidthPx + bufferPx) * 2

    val innerWidth = (actualSize.width - reduction).coerceAtLeast(0)
    val innerHeight = (actualSize.height - reduction).coerceAtLeast(0)

    // Center offset — how much to shift inward on each side
    val offsetX = ((actualSize.width - innerWidth) / 2f).coerceAtLeast(0f)
    val offsetY = ((actualSize.height - innerHeight) / 2f).coerceAtLeast(0f)

    return DrawingSafeArea(
        size = IntSize(innerWidth, innerHeight),
        offset = Offset(offsetX, offsetY)
    )
}

private fun createSignatureBitmap(
    strokes: List<List<Offset>>,
    width: Int,
    height: Int,
    strokeWidth: Float,
    strokeColor: Color,
    density: Density,
    background: Color = Color.White,
): ImageBitmap {
    val bitmap = ImageBitmap(width, height)
    val canvas = Canvas(bitmap)

    val drawScope = CanvasDrawScope()
    drawScope.draw(
        density = density,
        layoutDirection = LayoutDirection.Ltr,
        canvas = canvas,
        size = Size(width.toFloat(), height.toFloat())
    ) {
        // Draw white background
        drawRect(
            color = background,
            size = size
        )

        // Draw signature strokes
        strokes.forEach { points ->
            if (points.isNotEmpty()) {
                val path = createSmoothPath(points)
                drawPath(
                    path = path,
                    brush = SolidColor(strokeColor),
                    style = Stroke(width = strokeWidth)
                )
            }
        }
    }

    return bitmap
}

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
                exportWidth = 400.dp,
                exportHeight = 200.dp,
                onComplete = { bitmap ->
                    // Handle completion
                    println("Signature completed: ${bitmap?.width}x${bitmap?.height}")
                }
            )
        }
    }
}