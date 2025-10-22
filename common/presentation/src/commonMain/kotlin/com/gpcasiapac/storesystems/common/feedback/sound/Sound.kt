package com.gpcasiapac.storesystems.common.feedback.sound

/**
 * Cross-platform sound effect set. Extend as needed.
 */
enum class SoundEffect { Error, Success, Click }

/** Simple facade for playing short UI sounds. */
interface SoundPlayer {
    fun play(effect: SoundEffect)
    fun release() { /* no-op by default */ }
}

/** Default no-op implementation for platforms/tests without sound yet. */
class FakeSoundPlayer : SoundPlayer {
    override fun play(effect: SoundEffect) { /* no-op */ }
}