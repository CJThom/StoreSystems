package com.gpcasiapac.storesystems.feature.collect.presentation.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.niyajali.compose.sign.ComposeSign
import com.niyajali.compose.sign.rememberSignatureState
import org.jetbrains.compose.ui.tooling.preview.Preview



@Composable
fun SignatureInput(
    modifier: Modifier = Modifier,
    onSignatureUpdate: (ImageBitmap?) -> Unit,
) {
    var signature by remember { mutableStateOf<ImageBitmap?>(null) }

    ComposeSign(
        onSignatureUpdate = { signature = it },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )

//    val controller = remember { DrawController() }
//    Draw(drawController = controller, modifier = Modifier.fillMaxSize())

}


@Preview
@Composable
fun SignatureInputPreview() {
    Surface{
        SignatureInput(onSignatureUpdate = {})
    }
}