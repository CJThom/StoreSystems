package com.gpcasiapac.storesystems.foundation.design_system

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.vectorResource
import storesystems.foundation.design_system.generated.resources.Res
import storesystems.foundation.design_system.generated.resources.calendar_add_on
import storesystems.foundation.design_system.generated.resources.deployed_code
import storesystems.foundation.design_system.generated.resources.globe

//Note: Previews are not working with this icons in common main. (26/9/2025)
object MBoltIcons {
    val DeployedCode: ImageVector @Composable get() = vectorResource(Res.drawable.deployed_code)
    val Globe: ImageVector @Composable get() = vectorResource(Res.drawable.globe)
    val CalendarAddOn: ImageVector @Composable get() = vectorResource(Res.drawable.calendar_add_on)
}