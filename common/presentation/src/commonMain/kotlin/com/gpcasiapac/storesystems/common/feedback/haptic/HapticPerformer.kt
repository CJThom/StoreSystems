package com.gpcasiapac.storesystems.common.feedback.haptic

/** Facade to perform a haptic feedback pattern. */
interface HapticPerformer {
    fun perform(effect: HapticEffect)
}