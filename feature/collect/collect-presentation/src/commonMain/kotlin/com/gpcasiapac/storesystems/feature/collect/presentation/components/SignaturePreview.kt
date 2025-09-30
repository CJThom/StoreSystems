package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.foundation.design_system.Dimens

@Composable
fun SignaturePreview(
    modifier: Modifier = Modifier,
    strokes: List<List<Offset>>,
    strokeWidth: Dp = Dimens.Stroke.normal,
    strokeColor: Color = MaterialTheme.colorScheme.primary,
    height: Dp = 120.dp
) {
    val density = LocalDensity.current
    val strokePx = with(density) { strokeWidth.toPx() }

    Box(
        modifier = modifier.height(height)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            strokes.forEach { points ->
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