package com.gpcasiapac.storesystems.common.feedback.sound

/** Simple facade for playing short UI sounds. */
interface SoundPlayer {
    fun play(effect: SoundEffect)
    fun release() { /* no-op by default */ }
}