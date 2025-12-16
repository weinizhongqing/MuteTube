package com.tb.music.player.music.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tb.music.player.music.MusicPlayerHelper
import com.tb.music.player.music.db.MusicDataHelper

class TBPlayReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            MusicPlayerHelper.Action.ACTION_PLAY_PAUSE -> {
                MusicPlayerHelper.switchPlayState()
            }

            MusicPlayerHelper.Action.ACTION_NEXT_SONG -> {
                MusicPlayerHelper.playNext()
            }

            MusicPlayerHelper.Action.ACTION_PREVIOUS -> {
                MusicPlayerHelper.playPrevious()
            }

            MusicPlayerHelper.Action.ACTION_COLLECTION_CLICK -> {
                MusicPlayerHelper.playingMusic?.run {
                    val collectListId = MusicDataHelper.collectListId
                    if (MusicPlayerHelper.isCollection) {
                        MusicDataHelper.deleteMusicFromPlayList(collectListId, this, null)
                    } else {
                        MusicDataHelper.addMusicToPlayList(collectListId, this, null)
                    }
                }
            }

            MusicDataHelper.Action.ACTION_COLLECT_CHANGE -> {
                val musicId = intent.getLongExtra("tb_music_id", -2)
                if (musicId == -2L) return
                MusicPlayerHelper.collectChange(musicId)
            }
        }
    }

}