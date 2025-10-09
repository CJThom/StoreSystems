package com.gpcasiapac.storesystems.feature.collect.presentation.util

import androidx.compose.ui.graphics.ImageBitmap

expect fun imageBitmapToBase64Encoded(imageBitmap: ImageBitmap): String

expect fun base64EncodedToImageBitmap(base64Str: String): ImageBitmap