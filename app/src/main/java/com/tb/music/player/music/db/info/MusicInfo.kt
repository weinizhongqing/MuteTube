package com.tb.music.player.music.db.info

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_info")
class MusicInfo(
    @ColumnInfo("tb_song_path")
    val path: String,
    @ColumnInfo("tb_display_name")
    val displayName: String,
    @ColumnInfo("tb_thum_path")
    val thumPath: String,
    @ColumnInfo("tb_song_duration")
    val duration: Long,
    @ColumnInfo("tb_file_length")
    val length: Long,
    @ColumnInfo("tb_album_id")
    val albumId: Long,
    @ColumnInfo("tb_album_name")
    val albumName: String,
    @ColumnInfo("tb_singer_id")
    val singerId: Long,
    @ColumnInfo("tb_singer_name")
    val singerName: String,
    @ColumnInfo("tb_local_id")
    val localId: Long,
    @ColumnInfo("tb_album_thum_path")
    var albumThumPath: String = "",
    @ColumnInfo("tb_singer_thum_path")
    var singerThumPath: String = "",
    @ColumnInfo("tb_create_time")
    var createTime: Long = System.currentTimeMillis(),
    @ColumnInfo("tb_song_id")
    @PrimaryKey(autoGenerate = true)
    var songId: Long = 0,
    var isSelect: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString().orEmpty(),
        parcel.readLong(),
        parcel.readString().orEmpty(),
        parcel.readLong(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(path)
        parcel.writeString(displayName)
        parcel.writeString(thumPath)
        parcel.writeLong(duration)
        parcel.writeLong(length)
        parcel.writeLong(albumId)
        parcel.writeString(albumName)
        parcel.writeLong(singerId)
        parcel.writeString(singerName)
        parcel.writeLong(localId)
        parcel.writeString(albumThumPath)
        parcel.writeString(singerThumPath)
        parcel.writeLong(createTime)
        parcel.writeLong(songId)
        parcel.writeInt(isSelect)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MusicInfo> {
        override fun createFromParcel(parcel: Parcel): MusicInfo {
            return MusicInfo(parcel)
        }

        override fun newArray(size: Int): Array<MusicInfo?> {
            return arrayOfNulls(size)
        }
    }

}