package com.tb.music.player.tobe.view

import com.tb.music.player.tobe.models.SongItem

data class PlaylistContinuationPage(
    val songs: List<SongItem>,
    val continuation: String?,
)