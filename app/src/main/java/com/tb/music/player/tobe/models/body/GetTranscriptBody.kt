package com.tb.music.player.tobe.models.body

import com.tb.music.player.tobe.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class GetTranscriptBody(
    val context: Context,
    val params: String,
)
