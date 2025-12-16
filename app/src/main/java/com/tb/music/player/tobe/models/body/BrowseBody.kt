package com.tb.music.player.tobe.models.body

import com.tb.music.player.tobe.models.Context
import kotlinx.serialization.Serializable


@Serializable
data class BrowseBody(
    val context: Context,
    val browseId: String?,
    val params: String?,
    val continuation: String?
)