package com.gpcasiapac.storesystems.feature.collect.presentation.util

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.yield

/**
 * Reusable helpers to reveal a CTA (or any target Composable) inside lazy containers,
 * then optionally request focus on it. Works reliably even when BringIntoView is a no-op
 * for Lazy containers by first driving the container's scroll position.
 */
@Suppress("TooGenericExceptionCaught")
public suspend fun LazyGridState.revealAndFocus(
    bringIntoViewRequester: BringIntoViewRequester,
    focusRequester: FocusRequester,
    targetIndex: Int? = null,
) {
    scrollToIndexSafely(targetIndex)
    // Ask the target to ensure it's fully visible, then try focus
    try {
        bringIntoViewRequester.bringIntoView()
    } catch (_: Throwable) {
        // Ignore; focusing may still help on some platforms
    }
    try {
        focusRequester.requestFocus()
    } catch (_: Throwable) {
        // Ignore if focus isn't supported for this element/platform
    }
}

/** Same behavior for LazyColumn/LazyRow (LazyListState). */
@Suppress("TooGenericExceptionCaught")
public suspend fun LazyListState.revealAndFocus(
    bringIntoViewRequester: BringIntoViewRequester,
    focusRequester: FocusRequester,
    targetIndex: Int? = null,
) {
    scrollToIndexSafely(targetIndex)
    try {
        bringIntoViewRequester.bringIntoView()
    } catch (_: Throwable) {}
    try {
        focusRequester.requestFocus()
    } catch (_: Throwable) {}
}

// Internal helpers
private suspend fun LazyGridState.scrollToIndexSafely(targetIndex: Int?) {
    val index = targetIndex ?: (layoutInfo.totalItemsCount - 1)
    if (index >= 0) {
        try {
            animateScrollToItem(index)
            // Allow compose/layout to settle before doing bringIntoView/focus
            yield()
        } catch (_: Throwable) {
            // Ignore and let bringIntoView try
        }
    }
}

private suspend fun LazyListState.scrollToIndexSafely(targetIndex: Int?) {
    val index = targetIndex ?: (layoutInfo.totalItemsCount - 1)
    if (index >= 0) {
        try {
            animateScrollToItem(index)
            yield()
        } catch (_: Throwable) {
            // Ignore and let bringIntoView try
        }
    }
}

/**
 * Backward compatible wrapper. Prefer the extension functions above.
 */
@Deprecated("Use LazyGridState.revealAndFocusCta(bringIntoViewRequester, focusRequester, targetIndex)")
@Suppress("TooGenericExceptionCaught")
public suspend fun revealAndFocus(
    gridState: LazyGridState,
    bringIntoViewRequester: BringIntoViewRequester,
    focusRequester: FocusRequester,
) {
    gridState.revealAndFocus(
        bringIntoViewRequester = bringIntoViewRequester,
        focusRequester = focusRequester,
        targetIndex = null
    )
}
