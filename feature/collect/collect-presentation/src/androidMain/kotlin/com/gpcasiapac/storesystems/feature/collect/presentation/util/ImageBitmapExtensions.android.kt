package com.gpcasiapac.storesystems.feature.collect.presentation.util

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream

actual fun imageBitmapToBase64Encoded(imageBitmap: ImageBitmap): String {
    val androidBitmap = imageBitmap.asAndroidBitmap()
    val outputStream = ByteArrayOutputStream()
    androidBitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, outputStream)
    val imageBytes = outputStream.toByteArray()
    return Base64.encodeToString(imageBytes, Base64.DEFAULT)
}

actual fun base64EncodedToImageBitmap(base64Str: String): ImageBitmap {
    val imageBytes = Base64.decode(base64Str, Base64.DEFAULT)
    val androidBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    return androidBitmap.asImageBitmap()
}