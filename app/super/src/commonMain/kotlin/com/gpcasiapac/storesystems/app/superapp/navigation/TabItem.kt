package com.gpcasiapac.storesystems.app.superapp.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BackHand
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Outbox

@Serializable
sealed class TabItem(
    open val label: String,
    open val icon: ImageVector
) : NavKey {

    @Serializable
    data class Picking(
        override val label: String = "Picking",
        override val icon: ImageVector = Icons.Default.BackHand
    ) : TabItem(label, icon)

    @Serializable
    data class Collect(
        override val label: String = "Collect",
        override val icon: ImageVector = Icons.Default.Outbox
    ) : TabItem(label, icon)

    @Serializable
    data class History(
        override val label: String = "History",
        override val icon: ImageVector = Icons.Default.Checklist
    ) : TabItem(label, icon)

}