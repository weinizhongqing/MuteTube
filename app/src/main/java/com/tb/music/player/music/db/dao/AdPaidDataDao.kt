package com.tb.music.player.music.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tb.music.player.music.db.info.AdPaidData

@Dao
interface AdPaidDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvent(event: AdPaidData)

    @Query("SELECT * FROM tb_paid")
    fun getAlls(): List<AdPaidData>

    @Delete
    fun delete(event: AdPaidData)
}