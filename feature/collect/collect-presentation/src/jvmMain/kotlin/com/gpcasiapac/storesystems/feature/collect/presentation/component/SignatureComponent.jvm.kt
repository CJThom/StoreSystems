package com.gpcasiapac.storesystems.feature.collect.presentation.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
actual fun SignatureCanvas(
    modifier: Modifier,
    strokes: List<List<Offset>>,
    onStrokesChange: (List<List<Offset>>) -> Unit,
    strokeWidth: Dp,
    strokeColor: Color,
) {
    Text("SignatureCanvas not implemented on desktop")
}