package com.gpcasiapac.storesystems.common.feedback.haptic

/**
 * Cross-platform haptic effects used for short UI feedback.
 * Keep it minimal; extend when new UX cases appear.
 */
enum class HapticEffect { SelectionChanged, Success, Error }

/** Facade to perform a haptic feedback pattern. */
interface HapticPerformer {
    fun perform(effect: HapticEffect)
}

/** No-op implementation for platforms/tests without haptic support yet. */
class FakeHapticPerformer : HapticPerformer {
    override fun perform(effect: HapticEffect) { /* no-op */ }
}
