package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import storesystems.foundation.component.generated.resources.Res
import storesystems.foundation.component.generated.resources.gpc_square_logo

@Composable
fun GPCLogoIcon(
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(Res.drawable.gpc_square_logo),
        contentDescription = "GPC Logo",
        modifier = modifier,
    )
}

@Preview
@Composable
private fun GPCLogoIconPreview() {
    Image(
        painter = painterResource(Res.drawable.gpc_square_logo),
        contentDescription = "GPC Logo"
    )
}
