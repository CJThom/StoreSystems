package com.gpcasiapac.storesystems.foundation.component.icon

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.presentation.theme.borderStroke
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


object CustomerIconDefaults {

    const val SIZE = 26

}

@Composable
fun B2BIcon(
    modifier: Modifier = Modifier
) {
    Dimens
    Surface(
        modifier = modifier.size(CustomerIconDefaults.SIZE.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        border = MaterialTheme.borderStroke(),
        shape = CircleShape
    ) {
        Icon(
            imageVector = Icons.Outlined.BusinessCenter,
            contentDescription = "Business",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(Dimens.Space.extraSmall)
        )
    }
}

@Composable
fun B2CIcon(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(CustomerIconDefaults.SIZE.dp),
        border = MaterialTheme.borderStroke(),
        shape = CircleShape
    ) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = "Person",
            modifier = Modifier.padding(Dimens.Space.extraSmall)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun B2CIconPreview() {
    GPCTheme {
        Box(Modifier.padding(Dimens.Space.medium)) {
            B2CIcon()
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun B2BIconPreview() {
    GPCTheme {
        Box(Modifier.padding(Dimens.Space.medium)) {
            B2BIcon()
        }
    }
}
