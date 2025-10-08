package com.gpcasiapac.storesystems.feature.collect.presentation.component

import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.ink.authoring.InProgressStrokeId
import androidx.ink.authoring.InProgressStrokesFinishedListener
import androidx.ink.authoring.InProgressStrokesView
import androidx.ink.brush.Brush
import androidx.ink.brush.StockBrushes
import androidx.ink.strokes.Stroke
import androidx.input.motionprediction.MotionEventPredictor
import com.gpcasiapac.storesystems.foundation.design_system.Dimens

// Enhanced Signature Canvas using Ink API with common interface integration
// Read more about Ink API: https://developer.android.com/develop/ui/compose/touch-input/stylus-input/about-ink-api
// https://medium.com/@mmartosdev/android-ink-practical-guide-c403e1dd78f0

@Composable
actual fun SignatureCanvas(
    modifier: Modifier,
    strokes: List<List<Offset>>,
    onStrokesChange: (List<List<Offset>>) -> Unit,
    strokeWidth: Dp,
    strokeColor: Color,
) {
    SignatureCanvasWithInkApi(
        modifier = modifier,
        strokes = strokes,
        onStrokesChange = onStrokesChange,
        strokeWidth = strokeWidth,
        strokeColor = strokeColor
    )
}

/**
 * Advanced signature canvas using Android Ink API for enhanced stylus support and performance.
 * Integrates with the common SignatureCanvas interface while leveraging Ink API capabilities.
 */
@Composable
fun SignatureCanvasWithInkApi(
    modifier: Modifier = Modifier,
    strokes: List<List<Offset>>,
    onStrokesChange: (List<List<Offset>>) -> Unit,
    strokeWidth: Dp,
    strokeColor: Color,
) {
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { strokeWidth.toPx() }
    
    // Track local strokes for state management
    var localStrokes by remember { mutableStateOf(strokes) }
    var currentPointerId by remember { mutableStateOf<Int?>(null) }
    var currentStrokeId by remember { mutableStateOf<InProgressStrokeId?>(null) }
    var currentStrokePoints by remember { mutableStateOf<MutableList<Offset>>(mutableListOf()) }
    
    // Sync external strokes with local state
    LaunchedEffect(strokes) {
        if (strokes != localStrokes) {
            localStrokes = strokes
        }
    }
    
    val brush = Brush.createWithColorIntArgb(
        family = StockBrushes.markerV1,
        colorIntArgb = strokeColor.toArgb(),
        size = strokeWidthPx,
        epsilon = 0.1F
    )

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize()
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            factory = { context ->
                InProgressStrokesView(context).apply {
                    val motionEventPredictor = MotionEventPredictor.newInstance(rootView)
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                    )
                    
                    addFinishedStrokesListener(object : InProgressStrokesFinishedListener {
                        override fun onStrokesFinished(finishedStrokes: Map<InProgressStrokeId, Stroke>) {
                            // Convert finished Ink strokes to common interface format
                            finishedStrokes.forEach { (strokeId, stroke) ->
                                if (currentStrokePoints.isNotEmpty()) {
                                    val updatedStrokes = localStrokes + listOf(currentStrokePoints.toList())
                                    localStrokes = updatedStrokes
                                    onStrokesChange(updatedStrokes)
                                    currentStrokePoints.clear()
                                }
                            }
                            removeFinishedStrokes(finishedStrokes.keys)
                        }
                    })
                    
                    setOnTouchListener { view, event ->
                        try {
                            motionEventPredictor.record(event)
                            val predictedEvent = motionEventPredictor.predict()
                            
                            when (event.actionMasked) {
                                MotionEvent.ACTION_DOWN -> {
                                    view.requestUnbufferedDispatch(event)
                                    val pointerIndex = event.actionIndex
                                    val pointerId = event.getPointerId(pointerIndex)
                                    currentPointerId = pointerId
                                    
                                    // Start new stroke in Ink API
                                    currentStrokeId = startStroke(
                                        event = event, 
                                        pointerId = pointerId, 
                                        brush = brush
                                    )
                                    
                                    // Track stroke points for common interface
                                    val point = Offset(event.x, event.y)
                                    currentStrokePoints.clear()
                                    currentStrokePoints.add(point)
                                    true
                                }

                                MotionEvent.ACTION_MOVE -> {
                                    val pointerId = checkNotNull(currentPointerId)
                                    val strokeId = checkNotNull(currentStrokeId)

                                    for (pointerIndex in 0 until event.pointerCount) {
                                        if (event.getPointerId(pointerIndex) == pointerId) {
                                            // Update Ink API stroke
                                            addToStroke(
                                                event,
                                                pointerId,
                                                strokeId,
                                                predictedEvent,
                                            )
                                            
                                            // Track points for common interface
                                            val point = Offset(
                                                event.getX(pointerIndex),
                                                event.getY(pointerIndex)
                                            )
                                            currentStrokePoints.add(point)
                                        }
                                    }
                                    true
                                }

                                MotionEvent.ACTION_UP -> {
                                    val pointerIndex = event.actionIndex
                                    val pointerId = event.getPointerId(pointerIndex)
                                    check(pointerId == currentPointerId)
                                    val strokeId = checkNotNull(currentStrokeId)
                                    
                                    // Finish stroke in Ink API
                                    finishStroke(event, pointerId, strokeId)
                                    
                                    // Reset state
                                    currentPointerId = null
                                    currentStrokeId = null
                                    
                                    view.performClick()
                                    true
                                }

                                MotionEvent.ACTION_CANCEL -> {
                                    val pointerIndex = event.actionIndex
                                    val pointerId = event.getPointerId(pointerIndex)
                                    check(pointerId == currentPointerId)
                                    val strokeId = checkNotNull(currentStrokeId)
                                    
                                    // Cancel stroke in Ink API
                                    cancelStroke(strokeId, event)
                                    
                                    // Reset state
                                    currentPointerId = null
                                    currentStrokeId = null
                                    currentStrokePoints.clear()
                                    true
                                }

                                else -> false
                            }.also {
                                predictedEvent?.recycle()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            // Handle any errors gracefully
                            false
                        }
                    }
                }
            },
        )
    }
}

@Preview
@Composable
fun SignatureCanvasPreview() {
    var strokes by remember { mutableStateOf<List<List<Offset>>>(emptyList()) }
    MaterialTheme {
        Surface {
            SignatureCanvasWithInkApi(
                modifier = Modifier.fillMaxWidth(),
                strokes = strokes,
                onStrokesChange = { newStrokes -> strokes = newStrokes },
                strokeWidth = Dimens.Stroke.normal,
                strokeColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}