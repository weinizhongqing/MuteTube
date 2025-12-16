package com.tb.music.player.music.scan

import android.media.MediaScannerConnection
import android.os.Environment
import android.provider.MediaStore
import com.tb.music.player.TB
import com.tb.music.player.config.AppConfig
import com.tb.music.player.music.MusicPlayerHelper
import com.tb.music.player.music.db.MusicDataHelper
import com.tb.music.player.music.db.info.MusicInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

object ScanMusicHelper {

    val findMusicList = mutableListOf<MusicInfo>()

    suspend fun scanAddMusic(isFilter: Boolean,callback:suspend()->Unit) = withContext(Dispatchers.IO) {
        // 第一次扫描系统 MediaStore
        var musicList = querySystemMusic(isFilter)
        // 如果系统没索引但目录里有音频文件 → 强制扫描
        val audioFilePaths = getLocalAudioFiles() // 扫描存储卡原始文件
        if (audioFilePaths.isNotEmpty()) {
            scanFilesToMediaStore(audioFilePaths)
            delay(800)
            musicList = querySystemMusic(isFilter)
        }

        findMusicList.clear()
        findMusicList.addAll(musicList)
        callback.invoke()
    }

    private fun querySystemMusic(isFilter: Boolean): MutableList<MusicInfo> {
        val list = mutableListOf<MusicInfo>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DURATION
        )

        val selection = "${MediaStore.Audio.Media.MIME_TYPE} IN (?, ?, ?)"
        val args = arrayOf("audio/mpeg", "audio/mp4", "audio/ogg")

        val limitDuration = if (isFilter) 60_000L else -1L

        TB.instance.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            args,
            "${MediaStore.Audio.Media._ID} DESC"
        )?.use { cursor ->

            while (cursor.moveToNext()) {
                val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                if (limitDuration > 0 && duration > 0 && duration < limitDuration) continue
                val musicId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)) ?: ""
                val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)) ?: ""
                val album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)) ?: ""
                val singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)) ?: ""
                val singerId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID))
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))

                val music =  MusicInfo(path, name, path, duration, size, albumId, album, singerId, singer, musicId)

                list.add(music)
            }
        }

        return list
    }


    private fun getLocalAudioFiles(): List<String> {
        val root = Environment.getExternalStorageDirectory()
        return root.walkTopDown()
            .filter { file ->
                file.isFile && (
                        file.extension.equals("mp3", true) ||
                                file.extension.equals("m4a", true) ||
                                file.extension.equals("wav", true) ||
                                file.extension.equals("ogg", true)
                        )
            }
            .map { it.absolutePath }
            .toList()
    }

    private fun scanFilesToMediaStore(paths: List<String>) {
        MediaScannerConnection.scanFile(
            TB.instance,
            paths.toTypedArray(),
            null,
            null
        )
    }

    suspend fun addMusic(selectList: MutableList<MusicInfo>) {
        if (selectList.isEmpty()) return
        var isNotSetPlaylist = false
        if (AppConfig.playListId < 0) {
            isNotSetPlaylist = true
        }
        val localListId = MusicDataHelper.localListId
        val addPLayListIds = mutableListOf(
            localListId,
            MusicDataHelper.singleListId
        )
        MusicDataHelper.addMusic(selectList, addPLayListIds)
        if (isNotSetPlaylist) {
            AppConfig.playListId = localListId
            withContext(Dispatchers.Main) {
                MusicPlayerHelper.initPlayList()
            }
        }
    }

}