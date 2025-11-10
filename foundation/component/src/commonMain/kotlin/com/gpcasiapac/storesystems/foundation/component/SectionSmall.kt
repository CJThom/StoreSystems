package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

@Composable
fun SectionSmall(
    title: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium),
    content: @Composable (ColumnScope.() -> Unit) = {}
) {
    Column(modifier = modifier.padding(contentPadding)) {
        HeaderSmall(
            text = title,
            contentPadding = PaddingValues(),
            isLoading = isLoading
        )
        Spacer(Modifier.size(Dimens.Space.medium))
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }

}


@Preview(showBackground = true)
@Composable
private fun SectionSmallPreview() {
    GPCTheme {
        SectionSmall(
            title = "Section Title",
            content = {
                Text("Section Content")
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun SectionSmallPreviewLoading() {
    GPCTheme {
        SectionSmall(
            title = "Section Title",
            isLoading = true,
            content = {
                Text("Section Content")
            }
        )
    }
}