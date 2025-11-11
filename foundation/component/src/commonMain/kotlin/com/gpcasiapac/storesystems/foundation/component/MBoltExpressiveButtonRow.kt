package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedToggleButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TonalToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

/**
 * Expressive Material3 replacement for [MBoltSegmentedButtonRow].
 *
 * This is a generic, reusable single‑selection row of connected ToggleButtons built
 * with the Material 3 expressive API (ButtonGroup/ToggleButton).
 *
 * It intentionally mirrors the type parameters and parameters of MBoltSegmentedButtonRow
 * so callers can swap to this implementation with minimal changes.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T : MBoltSegmentedRowOptionDisplayParam, R> MBoltExpressiveButtonRow(
    modifier: Modifier = Modifier,
    selected: R?,
    onValueChange: (R) -> Unit,
    optionList: List<T>,
    selectionMapper: (T) -> R,
    equals: (T, R?) -> Boolean = { a, b -> a == b },
) {
    val haptics = LocalHapticFeedback.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
    ) {
        optionList.forEachIndexed { index, option ->
            OutlinedToggleButton(
                checked = equals(option, selected),
                onCheckedChange = { checked ->
                    if (checked) {
                        haptics.performHapticFeedback(HapticFeedbackType.VirtualKey)
                        val selectedOption = selectionMapper(option)
                        onValueChange(selectedOption)
                    }
                },
                enabled = option.enabled,
                // Connected shapes based on position in the group
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    optionList.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
                colors = ToggleButtonDefaults.outlinedToggleButtonColors(
                    checkedContainerColor = MaterialTheme.colorScheme.secondary,
                    checkedContentColor = MaterialTheme.colorScheme.onSecondary,
                ),
                // Semantics role as single-choice item
                modifier = Modifier
                    .weight(1f)
                    .semantics { role = Role.RadioButton },
                contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.ExtraSmallContainerHeight),
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = option.label,
                    modifier = Modifier.size(Dimens.Size.iconSmall),
                )
                Spacer(Modifier.size(Dimens.Space.extraSmall))
                Text(
                    text = option.label,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

// ---------- Previews ----------

private data class PreviewOption(
    val id: Int,
    override val icon: ImageVector,
    override val label: String,
    override val enabled: Boolean = true,
) : MBoltSegmentedRowOptionDisplayParam

@Preview(name = "Basic 3 options - middle selected")
@Composable
private fun MBoltExpressiveButtonRow_BasicPreview() {
    var selected by remember { mutableStateOf(2) }
    val options = listOf(
        PreviewOption(1, Icons.Outlined.Person, "Standard"),
        PreviewOption(2, Icons.Outlined.BusinessCenter, "Account"),
        PreviewOption(3, Icons.Outlined.LocalShipping, "Courier"),
    )
    GPCTheme {
        Surface {
            MBoltExpressiveButtonRow(
                selected = selected,
                onValueChange = { selected = it },
                optionList = options,
                selectionMapper = { it.id },
                equals = { a, b -> a.id == b }
            )
        }
    }
}

@Preview(name = "Disabled option present")
@Composable
private fun MBoltExpressiveButtonRow_DisabledPreview() {
    var selected by remember { mutableStateOf(1) }
    val options = listOf(
        PreviewOption(1, Icons.Outlined.Person, "Standard"),
        PreviewOption(2, Icons.Outlined.BusinessCenter, "Account", enabled = false),
        PreviewOption(3, Icons.Outlined.LocalShipping, "Courier"),
    )
    GPCTheme {
        Surface {
            MBoltExpressiveButtonRow(
                selected = selected,
                onValueChange = { selected = it },
                optionList = options,
                selectionMapper = { it.id },
                equals = { a, b -> a.id == b }
            )
        }
    }
}

@Preview(name = "Five options - connected group")
@Composable
private fun MBoltExpressiveButtonRow_FiveOptionsPreview() {
    var selected by remember { mutableStateOf(4) }
    val options = listOf(
        PreviewOption(1, Icons.Outlined.Person, "One"),
        PreviewOption(2, Icons.Outlined.Work, "Two"),
        PreviewOption(3, Icons.Outlined.BusinessCenter, "Three"),
        PreviewOption(4, Icons.Outlined.Home, "Four"),
        PreviewOption(5, Icons.Outlined.LocalShipping, "Five"),
    )
    GPCTheme {
        Surface {
            MBoltExpressiveButtonRow(
                selected = selected,
                onValueChange = { selected = it },
                optionList = options,
                selectionMapper = { it.id },
                equals = { a, b -> a.id == b }
            )
        }
    }
}