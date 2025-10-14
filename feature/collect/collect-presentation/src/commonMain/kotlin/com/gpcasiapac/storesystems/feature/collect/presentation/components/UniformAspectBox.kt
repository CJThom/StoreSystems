package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.min

@Composable
fun UniformAspectBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(targetWidth: Dp, targetHeight: Dp) -> Unit
) {
    BoxWithConstraints {
        val size = minOf(maxWidth, maxHeight)
        content(size, size)
    }
}