package com.gpcasiapac.storesystems.common.feedback.haptic

/** No-op implementation for platforms/tests without haptic support yet. */
class FakeHapticPerformer : HapticPerformer {
    override fun perform(effect: HapticEffect) { /* no-op */ }
}
