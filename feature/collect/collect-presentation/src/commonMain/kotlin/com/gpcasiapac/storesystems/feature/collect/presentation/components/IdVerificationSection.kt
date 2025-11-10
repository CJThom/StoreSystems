package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.presentation.compose.theme.BorderRole
import com.gpcasiapac.storesystems.common.presentation.compose.theme.borderStroke
import com.gpcasiapac.storesystems.foundation.component.HeaderSmall
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

@Composable
fun IdVerification(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium),
) {
    Column(modifier = modifier) {
        HeaderSmall(
            text = "ID Verification",
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding(),
                start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
                bottom = Dimens.Space.medium
            )
        )
        Column(
            modifier = Modifier
                .padding(
                    start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
                ),
            verticalArrangement = Arrangement.spacedBy(Dimens.Space.small)
        ) {
            CheckboxRow(
                checked = checked,
                onCheckedChange = { onCheckedChange(!checked) },
                text = "I have verified customer's ID"
            )
        }
    }
}


@Composable
fun CheckboxRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium),
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = checked,
                onClick = { onCheckedChange(!checked) },
                role = Role.Checkbox
            ),
        color = if (checked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        border = MaterialTheme.borderStroke(role = if (checked) BorderRole.Selected else BorderRole.Variant),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = null // null for accessibility; row handles clicks
            )
            Spacer(modifier = Modifier.width(Dimens.Space.medium))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

// ---- Previews ----
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