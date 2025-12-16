package com.tb.music.player.tobe.view

import com.tb.music.player.tobe.models.YTItem

data class ArtistItemsContinuationPage(
    val items: List<YTItem>,
    val continuation: String?,
)
