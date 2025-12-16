package com.tb.music.player.music.db.info

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_paid")
data class AdPaidData(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("paid_1") val id: Int = 0,
    @ColumnInfo("paid_2")
    val source: String,
    @ColumnInfo("paid_3")
    val platform: String,
    @ColumnInfo("paid_4")
    val adType: Int,
    @ColumnInfo("paid_5")
    val adFormat: String,
    @ColumnInfo("paid_6")
    val adUnitId: String,
    @ColumnInfo("paid_7")
    val value: Double,
    @ColumnInfo("paid_8")
    val currency: String,
    @ColumnInfo("paid_9")
    val isPrecache: Boolean,
    @ColumnInfo("paid_10")
    val customData: String // 保存 JSONObject.toString()
)