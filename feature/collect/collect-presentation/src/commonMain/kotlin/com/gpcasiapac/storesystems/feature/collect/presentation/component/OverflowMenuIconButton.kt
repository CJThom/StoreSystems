package com.gpcasiapac.storesystems.feature.collect.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

/**
 * A reusable overflow icon button that shows a DropdownMenu when pressed.
 *
 * - Handles its own expanded state.
 * - Exposes a menuContent slot to declare menu items and receive a dismiss callback.
 * - Anchors the menu to the icon button.
 */
@Composable
fun OverflowMenuIconButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector = Icons.Outlined.MoreVert,
    contentDescription: String = "More",
    menuContent: @Composable ColumnScope.(dismiss: () -> Unit) -> Unit,
) {
    val expanded = remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded.value = true },
            modifier = modifier,
            enabled = enabled,
        ) {
            Icon(imageVector = icon, contentDescription = contentDescription)
        }
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            // Provide a convenience dismiss callback to callers
            menuContent { expanded.value = false }
        }
    }
}


@Preview(name = "OverflowMenuIconButton", showBackground = true)
@Composable
private fun OverflowMenuIconButtonPreview() {
    GPCTheme {
        OverflowMenuIconButton { dismiss ->
            DropdownMenuItem(
                text = { Text("First") },
                onClick = { dismiss() }
            )
            DropdownMenuItem(
                text = { Text("Second") },
                onClick = { dismiss() }
            )
        }
    }
}
