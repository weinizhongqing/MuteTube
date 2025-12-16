package com.tb.music.player.tobe.view

import com.tb.music.player.tobe.models.YTItem

data class LibraryContinuationPage(
    val items: List<YTItem>,
    val continuation: String?,
)