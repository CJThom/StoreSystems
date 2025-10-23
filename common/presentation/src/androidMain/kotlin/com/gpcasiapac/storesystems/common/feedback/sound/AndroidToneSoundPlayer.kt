package com.gpcasiapac.storesystems.common.feedback.sound

import android.media.AudioManager
import android.media.ToneGenerator
import co.touchlab.kermit.Logger

/**
 * Android implementation using ToneGenerator for minimal, low-latency tones.
 */
class AndroidToneSoundPlayer(
    private val logger: Logger,
    streamType: Int = AudioManager.STREAM_MUSIC,
    volumePercent: Int = 80,
) : SoundPlayer {

    private val log = logger.withTag("SoundPlayer")

    private val toneGen: ToneGenerator? = try {
        ToneGenerator(streamType, volumePercent)
    } catch (t: Throwable) {
        log.e(t) { "Failed to create ToneGenerator" }
        null
    }

    override fun play(effect: SoundEffect) {
        val tg = toneGen ?: return
        val (tone, durationMs) = when (effect) {
            SoundEffect.Error -> ToneGenerator.TONE_PROP_NACK to 150
            SoundEffect.Success -> ToneGenerator.TONE_PROP_ACK to 150
            SoundEffect.Warning -> ToneGenerator.TONE_PROP_BEEP2 to 200
            SoundEffect.Click -> ToneGenerator.TONE_PROP_BEEP to 80
        }
        runCatching { tg.startTone(tone, durationMs) }
            .onFailure { log.e(it) { "Failed to play tone: $effect" } }
    }

    override fun release() {
        runCatching { toneGen?.release() }
            .onFailure { log.e(it) { "Failed to release ToneGenerator" } }
    }
}