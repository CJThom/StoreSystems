package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

/**
 * Toolbar scaffold for list items.
 *
 * Layout:
 * - Left: [content] (e.g., an AssistChip)
 * - Right: [actions] (regular IconButtons) and an optional overflow icon button.
 *
 * If [overflowMenu] is null, the overflow icon button is omitted entirely.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ListItemToolbarScaffold(
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    overflowMenu: (@Composable ColumnScope.(dismiss: () -> Unit) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small)
    ) {
        // Left-aligned content
        content()

        Spacer(Modifier.weight(1f))

        // Right-aligned actions (before overflow)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small),
            content = actions
        )

        // Optional overflow icon button at the far right
        if (overflowMenu != null) {
            OverflowMenuIconButton(
                modifier = Modifier.size(IconButtonDefaults.extraSmallContainerSize())
            ) { dismiss ->
                overflowMenu(dismiss)
            }
        }
    }
}


@Preview(name = "Toolbar with overflow", showBackground = true)
@Composable
private fun ListItemToolbarScaffoldWithOverflowPreview() {
    GPCTheme {
        ListItemToolbarScaffold(
            actions = {
                Text("Action")
            },
            overflowMenu = { dismiss ->
                DropdownMenuItem(
                    text = { Text("Item 1") },
                    onClick = { dismiss() }
                )
                DropdownMenuItem(
                    text = { Text("Item 2") },
                    onClick = { dismiss() }
                )
            }
        ) {
            Text("Picked 2h ago")
        }
    }
}

@Preview(name = "Toolbar without overflow", showBackground = true)
@Composable
private fun ListItemToolbarScaffoldNoOverflowPreview() {
    GPCTheme {
        ListItemToolbarScaffold(
            actions = {
                Text("Action")
            },
            overflowMenu = null
        ) {
            Text("Picked 2h ago")
        }
    }
}
