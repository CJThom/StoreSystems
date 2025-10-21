package com.gpcasiapac.storesystems.feature.collect.presentation.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Debounce infrastructure for ViewModels, centralized here:
 * - DebounceKey: enum for stable keys (avoid stringly-typed IDs)
 * - DebouncerDefaults: common debounce intervals
 * - Debounce: sealed class presets combining key + interval
 * - Debouncer: keyed debouncer with overload to accept a Debounce preset
 */
class Debouncer(private val scope: CoroutineScope) {
    private val jobs = mutableMapOf<DebounceKey, Job>()

    fun submit(key: DebounceKey, timeoutMs: Long, block: suspend () -> Unit) {
        jobs.remove(key)?.cancel()
        jobs[key] = scope.launch {
            if (timeoutMs > 0) delay(timeoutMs)
            block()
        }
    }

    fun submit(preset: DebouncePreset, block: suspend () -> Unit) {
        submit(preset.key, preset.interval.ms, block)
    }
}

/** Enum keys for debouncing. */
enum class DebounceKey {
    CollectingType,
    CourierName,
}

/** Common debounce durations. */
object DebouncerDefaults {
    enum class Interval(val ms: Long) {
        Short(150L),
        Medium(250L),
        Long(600L),
    }
}

/**
 * Marker interface for screen-defined debounce presets.
 * Allows Debouncer to stay generic while call sites provide
 * their own key + interval combos.
 */
interface DebouncePreset {
    val key: DebounceKey
    val interval: DebouncerDefaults.Interval
}