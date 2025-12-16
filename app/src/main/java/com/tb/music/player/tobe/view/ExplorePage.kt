package com.tb.music.player.tobe.view

import com.tb.music.player.tobe.models.AlbumItem

data class ExplorePage(
    val newReleaseAlbums: List<AlbumItem>,
    val moodAndGenres: List<MoodAndGenres.Item>,
)
