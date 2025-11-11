package com.gpcasiapac.storesystems.app.superapp.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.gpcasiapac.storesystems.app.superapp.navigation.TabItem

@Composable
fun TabsNavigationBar(
    selected: TabItem,
    tabList: List<TabItem> = listOf(TabItem.Picking(), TabItem.Collect(), TabItem.History()),
    onSelect: (TabItem) -> Unit,
) {
    NavigationBar {
        tabList.forEach { tab ->
            NavigationBarItem(
                selected = selected == tab,
                onClick = { onSelect(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label
                    )
                },
                label = { Text(tab.label) }
            )
        }
    }
}

