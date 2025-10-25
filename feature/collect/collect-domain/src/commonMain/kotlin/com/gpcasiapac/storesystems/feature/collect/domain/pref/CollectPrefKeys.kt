package com.gpcasiapac.storesystems.feature.collect.domain.pref

import com.gpcasiapac.storesystems.core.preferences.api.PreferenceKeyDef
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.builtins.nullable

object CollectPrefKeys {

    object SelectedWorkOrderId : PreferenceKeyDef<String?> {
        override val id: String = "collect.selected_work_order_id"
        override val serializer = String.serializer().nullable
        override val default: String? = null
    }

    @Serializable
    data class MainFilters(
        val customerTypes: Set<String> = emptySet(),
        val sort: String = "Recent",
        val showOnlyReady: Boolean = false,
    )

    object Filters : PreferenceKeyDef<MainFilters> {
        override val id: String = "collect.main_filters"
        override val serializer = MainFilters.serializer()
        override val default: MainFilters = MainFilters()
    }
}
