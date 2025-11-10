package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gpcasiapac.storesystems.foundation.component.CheckboxRow
import com.gpcasiapac.storesystems.foundation.component.SectionSmall
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme


@Composable
fun IdVerification(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium),
) {
    SectionSmall(
        title = "ID Verification",
        modifier = modifier,
        isLoading = isLoading,
        contentPadding = contentPadding
    ) {
        CheckboxRow(
            checked = checked,
            onCheckedChange = onCheckedChange,
            text = "I have verified customer's ID",
            isLoading = isLoading,
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun IdVerificationPreviewChecked() {
    GPCTheme {
        IdVerification(
            checked = true,
            onCheckedChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IdVerificationPreviewUnchecked() {
    GPCTheme {
        IdVerification(
            checked = false,
            onCheckedChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IdVerificationPreviewLoading() {
    GPCTheme {
        IdVerification(
            checked = false,
            onCheckedChange = {},
            isLoading = true
        )
    }
}