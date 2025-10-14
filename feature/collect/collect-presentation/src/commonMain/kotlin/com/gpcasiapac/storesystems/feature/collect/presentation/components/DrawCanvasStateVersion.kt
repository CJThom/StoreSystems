//TODO: REMOVE THIS FILE LATER.
//package com.gpcasiapac.storesystems.feature.collect.presentation.components
//
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.gestures.awaitEachGesture
//import androidx.compose.foundation.gestures.awaitFirstDown
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableLongStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.geometry.Rect
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.Canvas
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.ImageBitmap
//import androidx.compose.ui.graphics.Path
//import androidx.compose.ui.graphics.Shape
//import androidx.compose.ui.graphics.SolidColor
//import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
//import androidx.compose.ui.input.nestedscroll.NestedScrollSource
//import androidx.compose.ui.input.nestedscroll.nestedScroll
//import androidx.compose.ui.input.pointer.PointerEventPass
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.unit.Density
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.IntSize
//import androidx.compose.ui.unit.LayoutDirection
//import androidx.compose.ui.unit.dp
//import com.gpcasiapac.storesystems.foundation.design_system.Dimens
//import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
//import kotlinx.coroutines.delay
//import org.jetbrains.compose.ui.tooling.preview.Preview
//
//// New data class for normalized coordinates
//data class NormalizedOffset(
//    val x: Float, // 0.0 to 1.0
//    val y: Float  // 0.0 to 1.0
//)
//
//
//
//// Convert screen coordinates to normalized coordinates
//private fun screenToNormalized(
//    screenPoint: Offset,
//    drawingArea: DrawingSafeArea
//): NormalizedOffset {
//    return NormalizedOffset(
//        x = if (drawingArea.size.width > 0) {
//            ((screenPoint.x - drawingArea.offset.x) / drawingArea.size.width.toFloat()).coerceIn(0f, 1f)
//        } else 0f,
//        y = if (drawingArea.size.height > 0) {
//            ((screenPoint.y - drawingArea.offset.y) / drawingArea.size.height.toFloat()).coerceIn(0f, 1f)
//        } else 0f
//    )
//}
//
//// Convert normalized coordinates to screen coordinates
//private fun normalizedToScreen(
//    normalizedPoint: NormalizedOffset,
//    drawingArea: DrawingSafeArea
//): Offset {
//    return Offset(
//        x = drawingArea.offset.x + (normalizedPoint.x * drawingArea.size.width.toFloat()),
//        y = drawingArea.offset.y + (normalizedPoint.y * drawingArea.size.height.toFloat())
//    )
//}
//
//@Composable
//fun DrawCanvas(
//    key: Any,
//    modifier: Modifier = Modifier,
//    strokes: List<List<NormalizedOffset>>,
//    onStrokesChange: (List<List<NormalizedOffset>>) -> Unit,
//    contentPadding: PaddingValues = PaddingValues(horizontal = Dimens.Space.medium),
//    shape: Shape = MaterialTheme.shapes.medium,
//    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
//    background: Color = MaterialTheme.colorScheme.primaryContainer,
//    strokeWidth: Dp = 3.dp,
//    strokeColor: Color,
//    exportWidth: Dp? = null,
//    exportHeight: Dp? = null,
//    onComplete: ((ImageBitmap) -> Unit)? = null,
//    completionDelayMs: Long = 1200L, // 1.2 seconds of inactivity
//) {
//    val density = LocalDensity.current
//    val strokePx = with(density) { strokeWidth.toPx() }
//    var isDrawing by remember { mutableStateOf(false) }
//    var lastDrawTime by remember { mutableLongStateOf(0L) }
//
//    val scrollConnection = remember {
//        object : NestedScrollConnection {
//            override fun onPreScroll(
//                available: Offset,
//                source: NestedScrollSource
//            ): Offset {
//                // Allow parent scrolling when not actively drawing
//                return if (isDrawing) {
//                    // Consume all scroll events during drawing
//                    available
//                } else {
//                    Offset.Zero
//                }
//            }
//
//            override fun onPostScroll(
//                consumed: Offset,
//                available: Offset,
//                source: NestedScrollSource
//            ): Offset {
//                return Offset.Zero
//            }
//        }
//    }
//
//    // Local mutable copy to allow incremental updates then publish via callback
//    var localStrokes by remember { mutableStateOf(strokes) }
//
//    LaunchedEffect(strokes) {
//        // Sync with external strokes when they change from outside (not during active drawing)
//        if (localStrokes != strokes && !isDrawing) {
//            localStrokes = strokes
//        }
//    }
//
//    // Auto-completion effect (updated for normalized coordinates)
//    LaunchedEffect(lastDrawTime, localStrokes) {
//        if (onComplete != null && lastDrawTime > 0L && localStrokes.isNotEmpty()) {
//            delay(completionDelayMs)
//
//            if (System.currentTimeMillis() - lastDrawTime >= completionDelayMs) {
//                val exportWidthPx = exportWidth?.let { with(density) { it.toPx().toInt() } } ?: 800
//                val exportHeightPx = exportHeight?.let { with(density) { it.toPx().toInt() } } ?: 600
//
//                val bitmap = createSignatureBitmapFromNormalized(
//                    normalizedStrokes = localStrokes,
//                    width = exportWidthPx,
//                    height = exportHeightPx,
//                    strokeWidth = strokePx,
//                    strokeColor = strokeColor,
//                    density = density
//                )
//                onComplete(bitmap)
//            }
//        }
//    }
//
//    Canvas(
//        modifier = modifier
//            .padding(contentPadding)
//            .fillMaxSize() // Flexible sizing - no fixed aspect ratio
//            .nestedScroll(scrollConnection)
//            .background(background)
//            .border(border, shape)
//            .pointerInput(key) {
//                awaitEachGesture {
//                    val down = awaitFirstDown(pass = PointerEventPass.Initial)
//                    isDrawing = true
//                    lastDrawTime = System.currentTimeMillis()
//
//                    val drawingArea = calculateDrawingSafeArea(
//                        borderWidthPx = with(density) { border.width.roundToPx() },
//                        actualSize = IntSize(size.width.toInt(), size.height.toInt()),
//                        strokeWidthPx = strokePx
//                    )
//
//                    // Convert to normalized coordinates
//                    val normalizedDown = screenToNormalized(down.position, drawingArea)
//                    localStrokes = localStrokes + listOf(listOf(normalizedDown))
//                    onStrokesChange(localStrokes)
//
//                    do {
//                        val event = awaitPointerEvent(pass = PointerEventPass.Initial)
//                        var hasActivePointer = false
//
//                        event.changes.forEach { change ->
//                            if (change.id == down.id && change.pressed) {
//                                change.consume()
//                                lastDrawTime = System.currentTimeMillis()
//
//                                // Convert to normalized coordinates
//                                val normalizedPosition = screenToNormalized(change.position, drawingArea)
//
//                                val lastPath = localStrokes.lastOrNull() ?: emptyList()
//                                val updatedLast = lastPath + normalizedPosition
//                                localStrokes = localStrokes.dropLast(1) + listOf(updatedLast)
//                                onStrokesChange(localStrokes)
//                                hasActivePointer = true
//                            }
//                        }
//                    } while (hasActivePointer)
//
//                    isDrawing = false
//                }
//            },
//    ) {
//        val drawingArea = calculateDrawingSafeArea(
//            borderWidthPx = with(density) { border.width.roundToPx() },
//            actualSize = IntSize(size.width.toInt(), size.height.toInt()),
//            strokeWidthPx = strokePx
//        )
//
//        // Convert normalized coordinates to screen coordinates for drawing
//        localStrokes.forEach { normalizedStroke ->
//            if (normalizedStroke.isNotEmpty()) {
//                val screenPoints = normalizedStroke.map { normalizedPoint ->
//                    normalizedToScreen(normalizedPoint, drawingArea)
//                }
//                val path = createSmoothPath(screenPoints)
//                drawPath(
//                    path = path,
//                    brush = SolidColor(strokeColor),
//                    style = Stroke(width = strokePx)
//                )
//            }
//        }
//    }
//}
//
//private data class DrawingSafeArea(
//    val size: IntSize,
//    val offset: Offset = Offset.Zero,
//)
//
//private fun calculateDrawingSafeArea(
//    borderWidthPx: Int,
//    actualSize: IntSize,
//    bufferPx: Int = 8,
//    strokeWidthPx: Float = 0f
//): DrawingSafeArea {
//    // Account for stroke width to prevent visual overflow
//    val strokeBuffer = (strokeWidthPx / 2f).toInt().coerceAtLeast(0)
//    val reduction = (borderWidthPx + bufferPx + strokeBuffer) * 2
//
//    val innerWidth = (actualSize.width - reduction).coerceAtLeast(0)
//    val innerHeight = (actualSize.height - reduction).coerceAtLeast(0)
//
//    // Center offset — how much to shift inward on each side
//    val offsetX = ((actualSize.width - innerWidth) / 2f).coerceAtLeast(0f)
//    val offsetY = ((actualSize.height - innerHeight) / 2f).coerceAtLeast(0f)
//
//    return DrawingSafeArea(
//        size = IntSize(innerWidth, innerHeight),
//        offset = Offset(offsetX, offsetY)
//    )
//}
//
//private fun createSignatureBitmapFromNormalized(
//    normalizedStrokes: List<List<NormalizedOffset>>,
//    width: Int,
//    height: Int,
//    strokeWidth: Float,
//    strokeColor: Color,
//    density: Density,
//    background: Color = Color.White,
//): ImageBitmap {
//    val bitmap = ImageBitmap(width, height)
//    val canvas = Canvas(bitmap)
//
//    // Create drawing area for the bitmap
//    val bitmapDrawingArea = DrawingSafeArea(
//        size = IntSize(width, height),
//        offset = Offset.Zero
//    )
//
//    val drawScope = CanvasDrawScope()
//    drawScope.draw(
//        density = density,
//        layoutDirection = LayoutDirection.Ltr,
//        canvas = canvas,
//        size = Size(width.toFloat(), height.toFloat())
//    ) {
//        // Draw background
//        drawRect(color = background, size = size)
//
//        // Convert normalized coordinates to bitmap coordinates and draw
//        normalizedStrokes.forEach { normalizedStroke ->
//            if (normalizedStroke.isNotEmpty()) {
//                val bitmapPoints = normalizedStroke.map { normalizedPoint ->
//                    normalizedToScreen(normalizedPoint, bitmapDrawingArea)
//                }
//                val path = createSmoothPath(bitmapPoints)
//                drawPath(
//                    path = path,
//                    brush = SolidColor(strokeColor),
//                    style = Stroke(width = strokeWidth)
//                )
//            }
//        }
//    }
//
//    return bitmap
//}
//
//private fun createSmoothPath(points: List<Offset>): Path {
//    val path = Path()
//    if (points.isEmpty()) return path
//
//    if (points.size == 1) {
//        path.addOval(Rect(points[0], 2f))
//        return path
//    }
//
//    path.moveTo(points[0].x, points[0].y)
//
//    if (points.size == 2) {
//        // Simple line for two points
//        path.lineTo(points[1].x, points[1].y)
//    } else {
//        // Bézier curves for smooth drawing
//        for (i in 1 until points.size) {
//            val previous = if (i > 1) points[i - 2] else points[0]
//            val current = points[i - 1]
//            val next = points[i]
//            val following = if (i < points.size - 1) points[i + 1] else points[i]
//
//            // Calculate control points for smooth curves
//            val cp1 = Offset(
//                current.x + (next.x - previous.x) * 0.15f,
//                current.y + (next.y - previous.y) * 0.15f
//            )
//            val cp2 = Offset(
//                next.x - (following.x - current.x) * 0.15f,
//                next.y - (following.y - current.y) * 0.15f
//            )
//
//            path.cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, next.x, next.y)
//        }
//    }
//
//    return path
//}
//
//@Preview
//@Composable
//fun DrawCanvasPreview() {
//    var strokes by remember { mutableStateOf<List<List<NormalizedOffset>>>(emptyList()) }
//    GPCTheme {
//        Surface {
//            DrawCanvas(
//                strokes = strokes,
//                onStrokesChange = { strokes = it },
//                strokeWidth = Dimens.Stroke.thin,
//                strokeColor = Color.Black,
//                exportWidth = 400.dp,
//                exportHeight = 200.dp,
//                onComplete = { bitmap ->
//                    // Handle completion
//                    println("Signature completed: ${bitmap?.width}x${bitmap?.height}")
//                },
//                key = "Canvas"
//            )
//        }
//    }
//}
