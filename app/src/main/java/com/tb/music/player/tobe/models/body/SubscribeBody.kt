package com.tb.music.player.tobe.models.body

import com.tb.music.player.tobe.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class SubscribeBody(
    val channelIds: List<String>,
    val context: Context,
)