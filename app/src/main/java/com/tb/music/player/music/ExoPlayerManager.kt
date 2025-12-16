package com.tb.music.player.music

import android.annotation.SuppressLint
import android.util.Log
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import com.tb.music.player.TB
import com.tb.music.player.config.AppConfig
import com.tb.music.player.music.callback.OnPlayerCallback
import com.tb.music.player.music.db.MusicDataHelper
import com.tb.music.player.music.equalizer.EqualizerHelper
import com.tb.music.player.music.status.LoadStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("UnsafeOptInUsageError")
class ExoPlayerManager(private val callback: OnPlayerCallback) : Player.Listener {

    val player = ExoPlayer.Builder(TB.instance).build()

    private var playFailCount = 0
    private var playProgressJob: Job? = null

    init {
        player.addListener(this)
        player.setSeekParameters(SeekParameters.EXACT)
    }

    private fun startPlayProgress() {
        playProgressJob = TB.scope.launch {
            while (isActive) {
                withContext(Dispatchers.Main) {
                    callback.onPlayMusicProgressChange(player.currentPosition)
                }
                delay(50)
            }
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> playFailCount = 0
            Player.STATE_ENDED -> {
                if (AppConfig.stopMode == 5) {
                    AppConfig.stopMode = 0
                    AppConfig.stopTime = 0
                    return
                }
                callback.onPlayNextMusic(1)
            }
        }
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        if (audioSessionId != C.AUDIO_SESSION_ID_UNSET) {
            Log.d("ExoPlayerManager", "AudioSessionId changed: $audioSessionId")
            EqualizerHelper.instance.attachToSession(audioSessionId)
            MusicPlayerHelper.initPlayList()
        }
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        callback.onLoadMusicStateChange(
            if (isLoading) LoadStatus.LOADING else LoadStatus.READY
        )
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (isPlaying) {
            MusicPlayerHelper.addToRecent()
            startPlayProgress()
        } else {
            playProgressJob?.cancel()
            playProgressJob = null
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        val cause = error.cause?.toString() ?: ""

        // ▸ MediaCodec 错误
        if (cause.contains("MediaCodecAudioRenderer", ignoreCase = true)) {
            callback.onPlayNextMusic(2)
            return
        }

        // ▸ Source error
        if (error.errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS ||
            error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ||
            error.errorCode == PlaybackException.ERROR_CODE_IO_UNSPECIFIED
        ) {
            if (playFailCount < 3) {
                playFailCount++
                callback.onPlayMusicFail()
            } else {
                playFailCount = 0
                callback.onPlayNextMusic(2)
            }
            return
        }

        // ▸ 其他错误直接下一首
        callback.onPlayNextMusic(2)
    }
}
