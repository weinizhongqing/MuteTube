package com.tb.music.player.music.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tb.music.player.TB
import com.tb.music.player.music.db.dao.AdPaidDataDao
import com.tb.music.player.music.db.dao.MusicInfoDao
import com.tb.music.player.music.db.dao.PlayListDetailsInfoDao
import com.tb.music.player.music.db.dao.PlayListInfoDao
import com.tb.music.player.music.db.info.AdPaidData
import com.tb.music.player.music.db.info.MusicInfo
import com.tb.music.player.music.db.info.PlayListDetailsInfo
import com.tb.music.player.music.db.info.PlayListInfo

@Database(
    entities = [MusicInfo::class, PlayListInfo::class, PlayListDetailsInfo::class, AdPaidData::class],
    version = 1,
    exportSchema = false
)
abstract class MusicData : RoomDatabase() {

    companion object {
        val data by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Room.databaseBuilder(TB.instance, MusicData::class.java, "tb_song_data")
                .allowMainThreadQueries().build()
        }
    }

    abstract fun music(): MusicInfoDao

    abstract fun playList(): PlayListInfoDao

    abstract fun playListDetails(): PlayListDetailsInfoDao

    abstract fun adPaidData(): AdPaidDataDao

}