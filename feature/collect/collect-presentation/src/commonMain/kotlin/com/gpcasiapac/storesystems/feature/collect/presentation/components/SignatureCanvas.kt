package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

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

    // Local mutable copy to allow incremental updates then publish via callback
    // Remove dependency on strokes to prevent state resets during drawing
    var localStrokes by remember { mutableStateOf(strokes) }
    
    // Sync with external strokes only when they change from outside (not during active drawing)
    if (localStrokes != strokes && strokes.isNotEmpty() && localStrokes.isEmpty()) {
        localStrokes = strokes
    }

    Box(modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown(pass = PointerEventPass.Initial)
                        localStrokes = localStrokes + listOf(listOf(down.position))
                        onStrokesChange(localStrokes)
                        
                        do {
                            val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                            var hasActivePointer = false
                            
                            event.changes.forEach { change ->
                                if (change.id == down.id && change.pressed) {
                                    change.consume()
                                    val lastPath = localStrokes.lastOrNull() ?: emptyList()
                                    val updatedLast = lastPath + change.position
                                    localStrokes = localStrokes.dropLast(1) + listOf(updatedLast)
                                    onStrokesChange(localStrokes)
                                    hasActivePointer = true
                                }
                            }
                        } while (hasActivePointer)
                    }
                }
        ) {
            localStrokes.forEach { points ->
                if (points.size >= 2) {
                    val path = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        for (i in 1 until points.size) {
                            val p = points[i]
                            lineTo(p.x, p.y)
                        }
                    }
                    drawPath(
                        path = path,
                        brush = SolidColor(strokeColor),
                        style = Stroke(width = strokePx)
                    )
                } else if (points.size == 1) {
                    // Dot
                    drawCircle(
                        color = strokeColor,
                        radius = strokePx / 2,
                        center = points.first()
                    )
                }
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