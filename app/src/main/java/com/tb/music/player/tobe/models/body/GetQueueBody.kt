package com.tb.music.player.tobe.models.body

import com.tb.music.player.tobe.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class GetQueueBody(
    val context: Context,
    val videoIds: List<String>?,
    val playlistId: String?,
)
