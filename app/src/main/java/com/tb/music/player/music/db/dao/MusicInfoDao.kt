package com.tb.music.player.music.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tb.music.player.music.db.info.MusicInfo

@Dao
interface MusicInfoDao {

    @Query("SELECT * FROM music_info ORDER BY tb_create_time DESC")
    fun allMusic(): LiveData<MutableList<MusicInfo>>

    @Query("SELECT * FROM music_info WHERE tb_local_id=:localId")
    fun localMusic(localId: Long): MusicInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMusic(song: MusicInfo): Long

    @Query("SELECT * FROM music_info WHERE tb_song_id=:songId")
    fun getMusic(songId: Long): MusicInfo?

    @Delete
    fun delete(song: MusicInfo)

}