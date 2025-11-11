package com.gpcasiapac.storesystems.common.presentation.navigation

/**
 * Immutable List-based reducer helpers for navigation back stacks.
 */
object BackStackReducer {
    fun <K> push(stack: List<K>, key: K): List<K> = stack + key

    /**
     * Pop [count] entries from the end of the stack while keeping at least the root element.
     */
    fun <K> pop(stack: List<K>, count: Int = 1): List<K> {
        if (count <= 0) return stack
        val minSize = 1 // never pop the root; keep at least one element
        val targetSize = (stack.size - count).coerceAtLeast(minSize)
        return if (targetSize == stack.size) stack else stack.take(targetSize)
    }

    /**
     * Replace the top-most element with [key]. If the stack is empty, creates a single-element stack.
     */
    fun <K> replaceTop(stack: List<K>, key: K): List<K> =
        if (stack.isEmpty()) listOf(key) else stack.dropLast(1) + key

    fun <K> pushOrReplace(stack: List<K>, key: K): List<K> =
        if (stack.lastOrNull() == key) replaceTop(stack, key) else push(stack, key)

    // Stack: [ 1,2,3,4,5,6 ] When Key=7, afterKey=4 Transforms to [1,2,3,4,7]
    fun <K> truncateAfterAndPush(stack: List<K>, key: K, afterKey: K): List<K> {
        val index = stack.indexOf(afterKey)
        return if (index != -1) {
            val before = stack.take(index + 1)
            before + key
        } else {
            stack + key
        }
    }

}