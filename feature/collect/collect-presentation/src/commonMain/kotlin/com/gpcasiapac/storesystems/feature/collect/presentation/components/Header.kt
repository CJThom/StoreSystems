package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HeaderMedium(
    text: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    maxLines: Int = 1,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
            maxLines = maxLines,
            modifier = Modifier.placeholder(isLoading)
        )
    }
}


@Preview
@Composable
private fun HeaderPreview() {
    GPCTheme {
        Surface {
            HeaderMedium(
                text = "Header"
            )
        }
    }
}

@Preview
@Composable
private fun HeaderLoadingPreview() {
    GPCTheme {
        Surface {
            HeaderMedium(
                text = "Header",
                isLoading = true
            )
        }
    }
}


@Composable
fun HeaderSmall(
    text: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    maxLines: Int = 1,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = maxLines,
            modifier = Modifier.placeholder(isLoading)
        )
    }
}

@Preview
@Composable
private fun HeaderSmallPreview() {
    GPCTheme {
        Surface {
            HeaderSmall(text = "Recent searches")
        }
    }
}

@Preview
@Composable
private fun HeaderSmallLoadingPreview() {
    GPCTheme {
        Surface {
            HeaderSmall(
                text = "Recent searches",
                isLoading = true
            )
        }
    }
}
