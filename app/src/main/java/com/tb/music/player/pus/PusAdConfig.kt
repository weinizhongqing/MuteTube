package com.tb.music.player.pus

class PusAdConfig (
    val id: MutableList<NodeUnit>,
    var isEnable: Boolean,
    val cacheType: Type
)

data class NodeUnit(
    val id: String,
    val type: Type
)