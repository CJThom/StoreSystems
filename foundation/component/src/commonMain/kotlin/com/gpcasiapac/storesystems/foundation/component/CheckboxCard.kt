package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.presentation.compose.theme.borderStroke
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun CheckboxCard(
    isCheckable: Boolean,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = {
            if (enabled) {
                if (isCheckable) {
                    onCheckedChange(!isChecked)
                } else {
                    onClick?.invoke()
                }
            }
        },
        enabled = enabled
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
                .height(IntrinsicSize.Min)
        ) {
            AnimatedVisibility(visible = isCheckable) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    border = MaterialTheme.borderStroke(),
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = null,
                        enabled = enabled,
                        modifier = Modifier.padding(Dimens.Space.semiMedium)
                    )
                }
            }
            content()
        }
    }
}

@Composable
private fun SampleContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.Space.medium)
    ) {
        Text("Sample Content")
    }
}

@Preview
@Composable
private fun CheckboxCardPreviewCheckableChecked() {
    var isCheckedState by remember { mutableStateOf(true) }
    GPCTheme {
        CheckboxCard(
            isCheckable = true,
            isChecked = isCheckedState,
            onCheckedChange = { isCheckedState = it }
        ) {
            SampleContent()
        }
    }
}

@Preview
@Composable
private fun CheckboxCardPreviewCheckableNotChecked() {
    var isCheckedState by remember { mutableStateOf(false) }
    GPCTheme {
        CheckboxCard(
            isCheckable = true,
            isChecked = isCheckedState,
            onCheckedChange = { isCheckedState = it }
        ) {
            SampleContent()
        }
    }
}

@Preview
@Composable
private fun CheckboxCardPreviewNotCheckable() {
    var clickCount by remember { mutableStateOf(0) }
    GPCTheme {
        CheckboxCard(
            isCheckable = false,
            isChecked = false, // isChecked is irrelevant when not checkable, but required
            onCheckedChange = { },
            onClick = { clickCount++ }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.Space.medium)
            ) {
                Text("Click me! Clicked $clickCount times")
            }
        }
    }
}

@Preview
@Composable
private fun CheckboxCardPreviewDisabled() {
    var isCheckedState by remember { mutableStateOf(true) }
    GPCTheme {
        CheckboxCard(
            isCheckable = true,
            isChecked = isCheckedState,
            onCheckedChange = { isCheckedState = it },
            enabled = false
        ) {
            SampleContent()
        }
    }
}