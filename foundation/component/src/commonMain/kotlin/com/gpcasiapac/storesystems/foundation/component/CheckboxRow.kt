package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.common.presentation.compose.theme.BorderRole
import com.gpcasiapac.storesystems.common.presentation.compose.theme.borderStroke
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme


@Composable
fun CheckboxRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    useHaptics: Boolean = true,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium),
) {

    val haptics = LocalHapticFeedback.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .placeholder(isLoading)
            .selectable(
                selected = checked,
                onClick = {
                    if (useHaptics) {
                        if (!checked) {
                            haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                        } else {
                            haptics.performHapticFeedback(HapticFeedbackType.Reject)
                        }
                    }
                    onCheckedChange(!checked)
                },
                role = Role.Checkbox
            ),
        color = if (checked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        border = MaterialTheme.borderStroke(role = if (checked) BorderRole.Selected else BorderRole.Variant),
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

@Preview(showBackground = true)
@Composable
private fun IdVerificationPreviewChecked() {
    GPCTheme {
        CheckboxRow(
            text = "Id verification",
            checked = true,
            onCheckedChange = {},
            modifier = Modifier.padding(Dimens.Space.medium)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IdVerificationPreviewUnchecked() {
    GPCTheme {
        CheckboxRow(
            text = "Id verification",
            checked = false,
            onCheckedChange = {},
            modifier = Modifier.padding(Dimens.Space.medium)
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun IdVerificationPreviewLoading() {
    GPCTheme {
        CheckboxRow(
            text = "Id verification",
            checked = false,
            onCheckedChange = {},
            modifier = Modifier.padding(Dimens.Space.medium),
            isLoading = true
        )
    }
}