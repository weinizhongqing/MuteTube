package com.tb.music.player.music.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tb.music.player.music.db.info.PlayListDetailsInfo

@Dao
interface PlayListDetailsInfoDao {

    @Query("SELECT * FROM vb_list_detail WHERE tb_list_id=:listId ORDER BY tb_create_time DESC")
    fun getPlayerListDetails(listId: Long): MutableList<PlayListDetailsInfo>

    @Query("SELECT EXISTS(SELECT 1 FROM vb_list_detail WHERE tb_list_id=:listId AND tb_song_id=:musicId)")
    fun isExists(listId: Long, musicId: Long): Boolean

    @Query("SELECT COUNT(*) FROM vb_list_detail WHERE tb_list_id=:listId")
    fun playerListMusicCount(listId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPlayerDetails(playerListDetails: PlayListDetailsInfo): Long

    @Delete
    fun deletePlayerListDetail(playerListDetails: PlayListDetailsInfo)

    @Query("DELETE FROM vb_list_detail WHERE tb_list_id=:listId")
    fun deletePlayerListDetail(listId: Long)

    @Query("DELETE FROM vb_list_detail WHERE tb_list_id=:listId AND tb_song_id=:musicId")
    fun deletePlayerListDetail(listId: Long, musicId: Long)
    
}