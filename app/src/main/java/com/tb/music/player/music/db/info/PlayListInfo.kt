package com.tb.music.player.music.db.info

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "tb_list_info")
class PlayListInfo(
    @ColumnInfo("tb_list_type")
    val playerListType: Int,
    @ColumnInfo("tb_list_name")
    var playerListName: String = "",
    @ColumnInfo("tb_list_id")
    @PrimaryKey(autoGenerate = true)
    var playerListId: Long = 0
) : Parcelable {
    @ColumnInfo("tb_local_list_id")
    var localListId: Long = -1

    @ColumnInfo("tb_thum_path")
    var thumPath: String = ""

    @ColumnInfo("tb_create_time")
    var createTime: Long = System.currentTimeMillis()

    @Ignore
    var musicCount: Int = 0

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().orEmpty(),
        parcel.readLong()
    ) {
        localListId = parcel.readLong()
        thumPath = parcel.readString().orEmpty()
        createTime = parcel.readLong()
        musicCount = parcel.readInt()
    }


    object PlayListType {
        const val TYPE_LOCAL = 1
        const val TYPE_COLLECT = 2
        const val TYPE_ALBUM = 3
        const val TYPE_SINGER = 4
        const val TYPE_SINGLE = 5
        const val TYPE_CUSTOMIZE = 6
        const val TYPE_RECENTLY = 7
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(playerListType)
        parcel.writeString(playerListName)
        parcel.writeLong(playerListId)
        parcel.writeLong(localListId)
        parcel.writeString(thumPath)
        parcel.writeLong(createTime)
        parcel.writeInt(musicCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlayListInfo> {
        override fun createFromParcel(parcel: Parcel): PlayListInfo {
            return PlayListInfo(parcel)
        }

        override fun newArray(size: Int): Array<PlayListInfo?> {
            return arrayOfNulls(size)
        }
    }

}