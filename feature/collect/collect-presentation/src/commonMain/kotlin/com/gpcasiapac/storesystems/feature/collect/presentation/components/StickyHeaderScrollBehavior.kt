package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue

/**
 * StickyHeaderScrollBehavior defines the scroll behavior and appearance states for a StickyHeader.
 * Uses minimal state to track only whether the sticky header should be lifted.
 */
@Stable
interface StickyHeaderScrollBehavior {
    /**
     * Indicates whether the sticky header is in a "lifted" state.
     * When lifted, the sticky header should show elevated styling (shadow, different background).
     */
    val isLifted: Boolean
}

/**
 * Default implementation of StickyHeaderScrollBehavior that lifts the sticky header when the sticky header is fixed.
 * Uses LazyListState to detect when the sticky header is actually stuck at the top.
 *
 * @param lazyListState The LazyListState to monitor for sticky header position
 * @param stickyHeaderIndex The index of the sticky header item in the LazyColumn
 */
private class StickyHeaderScrollBehaviorImpl(
    private val lazyListState: LazyListState,
    private val stickyHeaderIndex: Int
) : StickyHeaderScrollBehavior {

    override val isLifted: Boolean by derivedStateOf {
        val firstVisible = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()
        // Sticky header is considered "stuck" when it's not the first visible item
        firstVisible?.index == stickyHeaderIndex
    }
}

/**
 * LazyGrid implementation of StickyHeaderScrollBehavior that lifts the sticky header when the sticky header is fixed.
 * Uses LazyGridState to detect when the sticky header is actually stuck at the top.
 *
 * @param lazyGridState The LazyGridState to monitor for sticky header position
 * @param stickyHeaderIndex The index of the sticky header item in the LazyGrid
 */
private class StickyHeaderLazyGridScrollBehaviorImpl(
    private val lazyGridState: LazyGridState,
    private val stickyHeaderIndex: Int
) : StickyHeaderScrollBehavior {

    override val isLifted: Boolean by derivedStateOf {
        val firstVisible = lazyGridState.layoutInfo.visibleItemsInfo.firstOrNull()
        // Sticky header is considered "stuck" when it's not the first visible item
        firstVisible?.index == stickyHeaderIndex
    }
}

/**
 * StickyBarDefaults provides default implementations and factory methods for StickyHeaderScrollBehavior.
 */
object StickyBarDefaults {

    /**
     * Creates a StickyHeaderScrollBehavior that lifts the sticky header when the sticky header is fixed.
     * Uses LazyListState to properly detect when the sticky header is stuck at the top.
     *
     * @param lazyListState The LazyListState to monitor for sticky header position
     * @param stickyHeaderIndex The index of the sticky header item in the LazyColumn
     * @return a [StickyHeaderScrollBehavior] that shows lifted styling when sticky header is fixed
     */
    fun liftOnScrollBehavior(lazyListState: LazyListState, stickyHeaderIndex: Int): StickyHeaderScrollBehavior = StickyHeaderScrollBehaviorImpl(lazyListState, stickyHeaderIndex)

    /**
     * Creates a StickyHeaderScrollBehavior that lifts the sticky header when the sticky header is fixed.
     * Uses LazyGridState to properly detect when the sticky header is stuck at the top.
     *
     * @param lazyGridState The LazyGridState to monitor for sticky header position
     * @param stickyHeaderIndex The index of the sticky header item in the LazyGrid
     * @return a [StickyHeaderScrollBehavior] that shows lifted styling when sticky header is fixed
     */
    fun liftOnScrollBehavior(lazyGridState: LazyGridState, stickyHeaderIndex: Int): StickyHeaderScrollBehavior = StickyHeaderLazyGridScrollBehaviorImpl(lazyGridState, stickyHeaderIndex)

}
