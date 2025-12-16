package com.tb.music.player.music

import android.annotation.SuppressLint
import android.media.audiofx.Equalizer
import com.google.android.gms.common.api.Api.BaseClientBuilder.API_PRIORITY_OTHER
class EqualizerManager {

    @SuppressLint("VisibleForTests")
    private val equalizer = Equalizer(API_PRIORITY_OTHER, MusicPlayerHelper.playerId)

    fun setUsePreset(index: Int) {
        setUsePreset(index.toShort())
    }

    private fun setUsePreset(preset: Short) {
        try {
            equalizer.usePreset(preset)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}