package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue

/**
 * FilterBarScrollBehavior defines the scroll behavior and appearance states for a FilterBar.
 * Uses minimal state to track only whether the filter bar should be lifted.
 */
@Stable
interface FilterBarScrollBehavior {
    /**
     * Indicates whether the filter bar is in a "lifted" state.
     * When lifted, the filter bar should show elevated styling (shadow, different background).
     */
    val isLifted: Boolean
}

/**
 * Default implementation of FilterBarScrollBehavior that lifts the filter bar when the sticky header is fixed.
 * Uses LazyListState to detect when the sticky header is actually stuck at the top.
 */
private class FilterBarScrollBehaviorImpl(
    private val lazyListState: LazyListState,
    private val stickyHeaderIndex: Int
) : FilterBarScrollBehavior {

    override val isLifted: Boolean by derivedStateOf {
        val firstVisible = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()
        // Sticky header is considered "stuck" when it's not the first visible item
        firstVisible?.index == stickyHeaderIndex
    }
}

/**
 * FilterBarDefaults provides default implementations and factory methods for FilterBarScrollBehavior.
 */
object FilterBarDefaults {

    /**
     * Creates a FilterBarScrollBehavior that lifts the filter bar when the sticky header is fixed.
     * Uses LazyListState to properly detect when the sticky header is stuck at the top.
     *
     * @param lazyListState The LazyListState to monitor for sticky header position
     * @param stickyHeaderIndex The index of the sticky header item in the LazyColumn
     * @return a [FilterBarScrollBehavior] that shows lifted styling when sticky header is fixed
     */
    fun liftOnScrollBehavior(lazyListState: LazyListState, stickyHeaderIndex: Int): FilterBarScrollBehavior = FilterBarScrollBehaviorImpl(lazyListState, stickyHeaderIndex)
}