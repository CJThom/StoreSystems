package com.gpcasiapac.storesystems.foundation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

/**
 * A lightweight scaffold for list items that provides a body [content] area and an optional
 * bottom toolbar row.
 *
 * Layout:
 * - content
 * - Spacer(weight = 1f)
 * - bottom toolbar (if provided)
 */
@Composable
fun ListItemScaffold(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        start = Dimens.Space.medium,
        top = Dimens.Space.medium,
        end = Dimens.Space.medium,
        bottom = Dimens.Space.small
    ),
    toolbar: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .padding(contentPadding),
    ) {
        content()

        Spacer(Modifier.weight(1f))

        if (toolbar != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                content = toolbar
            )
        }
    }
}


@Preview(name = "ListItemScaffold", showBackground = true)
@Composable
private fun ListItemScaffoldPreview() {
    GPCTheme {
        ListItemScaffold(
            contentPadding = PaddingValues(),
            toolbar = {
                Text("Toolbar content")
            }
        ) {
            Text("Body content goes here")
        }
    }
}
