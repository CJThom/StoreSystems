package com.gpcasiapac.storesystems.feature.collect.presentation.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BackHand
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gpcasiapac.storesystems.common.kotlin.extension.toLocalDateTimeString
import com.gpcasiapac.storesystems.common.kotlin.extension.toTimeAgoString
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.foundation.placeholder
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlin.time.Instant

/**
 * Common toolbar content displaying the picked-at time as an AssistChip with toggle between
 * relative and absolute time formats.
 */
@Composable
fun CollectPickedAtChip(
    pickedAt: Instant,
    modifier: Modifier = Modifier,
    showAbsoluteTimeInitially: Boolean = false,
    isLoading: Boolean = false
) {
    val showAbsoluteTime = remember { mutableStateOf(showAbsoluteTimeInitially) }
    AssistChip(
        modifier = modifier,
        label = {
            AnimatedContent(targetState = showAbsoluteTime.value) { showAbs ->
                val text =
                    if (showAbs) pickedAt.toLocalDateTimeString() else pickedAt.toTimeAgoString()
                Text(text = text, modifier = Modifier.placeholder(isLoading))
            }
        },
        colors = AssistChipDefaults.assistChipColors(
            leadingIconContentColor = MaterialTheme.colorScheme.tertiary
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.BackHand,
                contentDescription = null,
                modifier = Modifier.size(SuggestionChipDefaults.IconSize)
            )
        },
        onClick = { showAbsoluteTime.value = !showAbsoluteTime.value }
    )
}


@Preview(name = "PickedAt Relative", showBackground = true)
@Composable
private fun CollectPickedAtChipRelativePreview() {
    GPCTheme {
        CollectPickedAtChip(pickedAt = Instant.parse("2025-09-29T00:00:00Z"))
    }
}

@Preview(name = "PickedAt Absolute", showBackground = true)
@Composable
private fun CollectPickedAtChipAbsolutePreview() {
    GPCTheme {
        CollectPickedAtChip(
            pickedAt = Instant.parse("2025-09-29T00:00:00Z"),
            showAbsoluteTimeInitially = true
        )
    }
}
