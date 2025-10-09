package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.gpcasiapac.storesystems.feature.collect.presentation.util.base64EncodedToImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun Base64ImageView(
    base64: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    // Decode Base64 off the UI thread only when it changes
    val imageBitmap by produceState<ImageBitmap?>(null, base64) {
        value = withContext(Dispatchers.IO) { base64?.let { base64EncodedToImageBitmap(it) } }
    }

    imageBitmap?.let {
        Image(
            bitmap = it,
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}