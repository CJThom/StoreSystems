package com.gpcasiapac.storesystems.common.feedback.sound

/** Default no-op implementation for platforms/tests without sound yet. */
class FakeSoundPlayer : SoundPlayer {
    override fun play(effect: SoundEffect) { /* no-op */ }
}