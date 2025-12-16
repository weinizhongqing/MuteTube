package com.tb.music.player.music.callback

import com.tb.music.player.music.status.LoadStatus


interface OnPlayerCallback {
    fun onPlayNextMusic(type: Int)
    fun onPlayMusicFail()
    fun onLoadMusicStateChange(state: LoadStatus)
    fun onPlayMusicProgressChange(progress: Long)
}