package com.tb.music.player.music.db

import android.content.Intent
import com.tb.music.player.TB
import com.tb.music.player.config.AppConfig
import com.tb.music.player.music.MusicPlayerHelper
import com.tb.music.player.music.db.info.MusicInfo
import com.tb.music.player.music.db.info.PlayListDetailsInfo
import com.tb.music.player.music.db.info.PlayListInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object MusicDataHelper {

    private val db by lazy { MusicData.data }

    val collectListId: Long
        get() {
            return AppConfig.getDefaultPlayListId(PlayListInfo.PlayListType.TYPE_COLLECT)
        }

    val recentlyListId: Long
        get() {
            return AppConfig.getDefaultPlayListId(PlayListInfo.PlayListType.TYPE_RECENTLY)
        }

    val singleListId: Long
        get() {
            return AppConfig.getDefaultPlayListId(PlayListInfo.PlayListType.TYPE_SINGLE)
        }

    val localListId: Long
        get() {
            return AppConfig.getDefaultPlayListId(PlayListInfo.PlayListType.TYPE_LOCAL)
        }

    fun addDefaultPlayList() {
        TB.scope.launch {
            val listTypes = mutableListOf(
                PlayListInfo.PlayListType.TYPE_COLLECT,
                PlayListInfo.PlayListType.TYPE_LOCAL,
                PlayListInfo.PlayListType.TYPE_SINGLE,
                PlayListInfo.PlayListType.TYPE_RECENTLY,
            )
            listTypes.forEach {
                if (AppConfig.getDefaultPlayListId(it) == -1L) {

                    AppConfig.setDefaultPlayListId(it, db.playList().addPlayerList(PlayListInfo(it)))
                }
            }
        }
    }

    fun isCollect(music: MusicInfo): Boolean = db.playListDetails().isExists(
        collectListId, music.songId
    )

    fun deleteMusicFromPlayList(listId: Long, music: MusicInfo, complete: (() -> Unit)?) {
        TB.scope.launch {
            deleteMusicFromPlayList(listId, music)
            withContext(Dispatchers.Main) {
                complete?.invoke()
            }
        }
    }

    suspend fun deleteMusicFromPlayList(listId: Long, music: MusicInfo) {
        db.playListDetails().deletePlayerListDetail(listId, music.songId)
        sendPlayListContentChange(listId)
        if (listId == collectListId) {
            sendCollectChange(music)
        }
        if (listId == AppConfig.playListId) {
            withContext(Dispatchers.Main) {
                MusicPlayerHelper.deleteMusic(music)
            }
        }
    }

    fun addMusicToPlayList(listId: Long, music: MusicInfo, complete: (() -> Unit)?) {
        TB.scope.launch {
            addMusicToPlayList(listId, music)
            withContext(Dispatchers.Main) {
                complete?.invoke()
            }
        }
    }

    suspend fun addMusicToPlayList(listId: Long, music: MusicInfo) {
        db.playListDetails().addPlayerDetails(PlayListDetailsInfo(listId, music.songId))
        sendPlayListContentChange(listId)
        if (listId == collectListId) {
            sendCollectChange(music)
        }
        if (listId == AppConfig.playListId) {
            withContext(Dispatchers.Main) {
                MusicPlayerHelper.addMusic(music)
            }
        }
    }

    fun addPlayList(playList: PlayListInfo, complete: (() -> Unit)?) {
        TB.scope.launch {
            addPlayList(playList)
            withContext(Dispatchers.Main) {
                complete?.invoke()
            }
        }
    }

    suspend fun addPlayList(playList: PlayListInfo): Long {
        val id = db.playList().addPlayerList(playList)
        playList.playerListId = id
        sendPlayListChange(id)
        return id
    }

    fun deletePlayList(listId: Long, complete: (() -> Unit)?) {
        TB.scope.launch {
            deletePlayList(listId)
            withContext(Dispatchers.Main) {
                complete?.invoke()
            }
        }
    }

    suspend fun deletePlayList(listId: Long) {
        db.playList().deletePlayerList(listId)
        db.playListDetails().deletePlayerListDetail(listId)
        sendPlayListContentChange(listId)
        sendPlayListChange(listId)
        if (listId == collectListId) {
            sendCollectChange(null)
        }
        if (listId == AppConfig.playListId) {
            withContext(Dispatchers.Main) {
                MusicPlayerHelper.deleteAllMusic()
            }
        }
    }

    fun getPlayList(listType: Int, complete: (MutableList<PlayListInfo>) -> Unit) {
        TB.scope.launch {
            val playList = db.playList().getPlayerList(listType)
            playList.forEach {
                it.musicCount = db.playListDetails().playerListMusicCount(it.playerListId)
            }
            withContext(Dispatchers.Main) {
                complete.invoke(playList)
            }
        }
    }

    fun getAllPlayList(isAdd: Boolean, complete: (MutableList<PlayListInfo>) -> Unit) {
        TB.scope.launch {
            val playList = if (isAdd) {
                db.playList().getAllAddPlayerList()
            } else {
                db.playList().getAllPlayerList()
            }
            playList.forEach {
                it.musicCount = db.playListDetails().playerListMusicCount(it.playerListId)
            }
            withContext(Dispatchers.Main) {
                complete.invoke(playList)
            }
        }
    }

    fun getPlayListMusic(listId: Long, complete: (MutableList<MusicInfo>) -> Unit) {
        TB.scope.launch {
            val musicList = mutableListOf<MusicInfo>()
            db.playListDetails().getPlayerListDetails(listId).forEach {
                db.music().getMusic(it.musicId)?.let {
                    musicList.add(it)
                }
            }
            withContext(Dispatchers.Main) {
                complete.invoke(musicList)
            }
        }
    }

    suspend fun addMusic(musicList: MutableList<MusicInfo>, addPlayListIds: MutableList<Long>) {
        musicList.forEach {
            addMusic(it, addPlayListIds)
        }
    }

    suspend fun addMusic(music: MusicInfo, addPlayListIds: MutableList<Long>) {
        var musicInfo = db.music().localMusic(music.localId)
        if (musicInfo != null) {
            musicInfo.createTime = music.createTime
        } else {
            musicInfo = music
        }
        val musicId = db.music().addMusic(musicInfo)
        musicInfo.songId = musicId
        addPlayList(musicInfo)
        addPlayListIds.forEach {
            addMusicToPlayList(it, musicInfo)
        }
    }

    private suspend fun addPlayList(music: MusicInfo) {
        val albumInfo =
            db.playList().getLocalPlayerList(PlayListInfo.PlayListType.TYPE_ALBUM, music.albumId)
        val albumListId = albumInfo?.playerListId ?: addPlayList(PlayListInfo(
            PlayListInfo.PlayListType.TYPE_ALBUM, music.albumName
        ).apply {
            localListId = music.albumId
            thumPath = music.albumThumPath
        })
        addMusicToPlayList(albumListId, music)
        val singerInfo =
            db.playList().getLocalPlayerList(PlayListInfo.PlayListType.TYPE_SINGER, music.singerId)
        val singerListId = singerInfo?.playerListId ?: addPlayList(PlayListInfo(
            PlayListInfo.PlayListType.TYPE_SINGER, music.singerName
        ).apply {
            localListId = music.singerId
            thumPath = music.singerThumPath
        })
        addMusicToPlayList(singerListId, music)
    }

    private fun sendCollectChange(music: MusicInfo?) {
        TB.instance.sendBroadcast(Intent(Action.ACTION_COLLECT_CHANGE).apply {
            putExtra("tb_music_id", music?.songId ?: -1)
            `package` = TB.instance.packageName
        })
    }

    private fun sendPlayListContentChange(listId: Long) {
        TB.instance.sendBroadcast(Intent(Action.ACTION_PLAY_LIST_CONTENT_CHANGE).apply {
            putExtra("tb_list_id", listId)
            `package` = TB.instance.packageName
        })
    }

    private fun sendPlayListChange(listId: Long) {
        TB.instance.sendBroadcast(Intent(Action.ACTION_PLAY_LIST_CHANGE).apply {
            putExtra("tb_list_id", listId)
            `package` = TB.instance.packageName
        })
    }

    object Action {
        const val ACTION_PLAY_LIST_CHANGE = "com.tb.player.PLAY_LIST_CHANGE"
        const val ACTION_PLAY_LIST_CONTENT_CHANGE = "com.tb.player.PLAY_LIST_CONTENT_CHANGE"
        const val ACTION_COLLECT_CHANGE = "com.tb.player.COLLECT_CHANGE"
    }
}