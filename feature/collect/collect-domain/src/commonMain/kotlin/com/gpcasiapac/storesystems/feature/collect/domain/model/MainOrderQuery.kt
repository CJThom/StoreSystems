package com.gpcasiapac.storesystems.feature.collect.domain.model

// Main-list query for filters + sort
data class MainOrderQuery(
    val customerTypes: Set<CustomerType>,
    val sort: SortOption,
)