package com.gpcasiapac.storesystems.foundation.component.icon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme


object CustomerIconDefaults {

    const val SIZE = 22
    const val PADDING = 3

}

@Composable
fun B2BIcon(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    Icon(
        modifier = modifier
            .placeholder(isLoading, shape = CircleShape)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .size(CustomerIconDefaults.SIZE.dp)
            .padding(CustomerIconDefaults.PADDING.dp),
        imageVector = Icons.Default.Business, contentDescription = "Business",
        tint = MaterialTheme.colorScheme.onPrimary,
    )
}

@Composable
fun B2CIcon(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    Icon(
        modifier = modifier
            .placeholder(isLoading, shape = CircleShape)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .size(CustomerIconDefaults.SIZE.dp)
            .padding(3.dp),
        imageVector = Icons.Default.Person,
        contentDescription = "Person",
        tint = MaterialTheme.colorScheme.primary,
    )
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
fun B2CIconLoadingPreview() {
    GPCTheme {
        Box(Modifier.padding(Dimens.Space.medium)) {
            B2CIcon(isLoading = true)
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

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun B2BIconLoadingPreview() {
    GPCTheme {
        Box(Modifier.padding(Dimens.Space.medium)) {
            B2BIcon(isLoading = true)
        }
    }
}
