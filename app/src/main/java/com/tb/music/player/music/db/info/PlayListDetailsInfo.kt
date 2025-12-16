package com.tb.music.player.music.db.info

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "vb_list_detail",
    primaryKeys = ["tb_list_id", "tb_song_id"]
)
class PlayListDetailsInfo(
    @ColumnInfo("tb_list_id")
    val playerListId: Long,
    @ColumnInfo("tb_song_id")
    val musicId: Long,
    @ColumnInfo("tb_create_time")
    var createTime: Long = System.currentTimeMillis()
)