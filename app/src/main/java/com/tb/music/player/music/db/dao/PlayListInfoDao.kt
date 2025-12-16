package com.tb.music.player.music.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tb.music.player.music.db.info.PlayListInfo

@Dao
interface PlayListInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPlayerList(playerList: PlayListInfo): Long

    @Query("SELECT * FROM tb_list_info WHERE tb_list_id=:listId")
    fun getPlayerList(listId: Long): PlayListInfo?

    @Query("SELECT * FROM tb_list_info WHERE tb_local_list_id=:localId AND tb_list_type=:listType")
    fun getLocalPlayerList(listType: Int, localId: Long): PlayListInfo?

    @Query("SELECT * FROM tb_list_info WHERE tb_list_type=:listType ORDER BY tb_create_time ASC")
    fun getPlayerList(listType: Int): MutableList<PlayListInfo>

    @Query("SELECT * FROM tb_list_info WHERE tb_list_type=:listType AND tb_list_id=:listId")
    fun getPlayerList(listType: Int, listId: Long): PlayListInfo?

    @Query("DELETE FROM tb_list_info WHERE tb_list_id=:listId")
    fun deletePlayerList(listId: Long)

    @Delete
    fun deletePlayerList(playerList: PlayListInfo)

    @Query("SELECT * FROM tb_list_info WHERE tb_list_type NOT IN (${PlayListInfo.PlayListType.TYPE_COLLECT},${PlayListInfo.PlayListType.TYPE_LOCAL}, ${PlayListInfo.PlayListType.TYPE_SINGER}, ${PlayListInfo.PlayListType.TYPE_SINGLE}, ${PlayListInfo.PlayListType.TYPE_ALBUM}) ORDER BY tb_create_time ASC")
    fun getAllAddPlayerList(): MutableList<PlayListInfo>

    @Query("SELECT * FROM tb_list_info WHERE tb_list_type NOT IN (${PlayListInfo.PlayListType.TYPE_SINGER}, ${PlayListInfo.PlayListType.TYPE_SINGLE}, ${PlayListInfo.PlayListType.TYPE_ALBUM}) ORDER BY tb_create_time ASC")
    fun getAllPlayerList(): MutableList<PlayListInfo>
    
}