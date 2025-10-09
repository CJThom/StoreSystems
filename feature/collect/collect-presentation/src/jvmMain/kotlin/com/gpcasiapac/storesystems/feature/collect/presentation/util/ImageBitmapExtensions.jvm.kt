package com.gpcasiapac.storesystems.feature.collect.presentation.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.imageio.ImageIO

actual fun imageBitmapToBase64Encoded(imageBitmap: ImageBitmap): String {
    val bufferedImage = imageBitmap.toAwtImage()
    val outputStream = ByteArrayOutputStream()
    ImageIO.write(bufferedImage, "PNG", outputStream)
    val imageBytes = outputStream.toByteArray()
    return Base64.getEncoder().encodeToString(imageBytes)
}

actual fun base64EncodedToImageBitmap(base64Str: String): ImageBitmap {
    val imageBytes = Base64.getDecoder().decode(base64Str)
    val inputStream = ByteArrayInputStream(imageBytes)
    val bufferedImage = ImageIO.read(inputStream)
    return bufferedImage.toComposeImageBitmap()
}