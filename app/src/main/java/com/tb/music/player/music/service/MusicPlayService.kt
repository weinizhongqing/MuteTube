package com.tb.music.player.music.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import com.tb.music.player.R
import com.tb.music.player.TB
import com.tb.music.player.music.MusicPlayerHelper
import com.tb.music.player.music.db.MusicDataHelper
import com.tb.music.player.music.db.info.MusicInfo
import com.tb.music.player.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicPlayService : LifecycleService(){

    companion object {
        private var isRunning = false

        fun startPlayService() {
            if (isRunning || !hasNotifyPermission()) return
            try {
                Log.d("MusicPlayService", "startPlayService")
                ContextCompat.startForegroundService(
                    TB.instance,
                    Intent(TB.instance, MusicPlayService::class.java)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        private fun hasNotifyPermission(): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    TB.instance,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        }
    }

    private val musicPlayerChannel = "tb_player"
    private val musicPlayerNotifyId = 826323
    private var receiverCode = 463742
    private var activityCode = 78289


    override fun onCreate() {
        super.onCreate()
        isRunning = true
        Log.d("MusicPlayService", "onStartCommand")
        sendNotify(true, 0, MusicPlayerHelper.isPlaying)
        observePlay()
    }


    private fun observePlay() {
        var alreadySendProgress = 0L
        MusicPlayerHelper.run {
            playMusic.observe(this@MusicPlayService) {
                alreadySendProgress = 0
                sendNotify(false, alreadySendProgress, isPlaying)
            }
            playProgress.observe(this@MusicPlayService) {
                if (getNotifyProgress(alreadySendProgress, it.first) == getNotifyProgress(
                        it.second,
                        it.first
                    )
                ) return@observe
                alreadySendProgress = it.second
                sendNotify(false, alreadySendProgress, isPlaying)
            }
            playState.observe(this@MusicPlayService) {
                alreadySendProgress = playMusicProgress
                sendNotify(false, alreadySendProgress, isPlaying)
            }
            collection.observe(this@MusicPlayService) {
                alreadySendProgress = playMusicProgress
                sendNotify(false, alreadySendProgress, isPlaying)
            }
        }
    }


    private fun getNotifyProgress(progress: Long, duration: Long): Int {
        return ((progress / duration.toFloat()) * 100).toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("MusicPlayService", "onStartCommand")
        sendNotify(false, MusicPlayerHelper.playMusicProgress, MusicPlayerHelper.isPlaying)
        return START_NOT_STICKY
    }


    private fun sendNotify(init: Boolean, progress: Long, isPlaying: Boolean) {
        Log.d("MusicPlayService", "sendNotify")
        val music = MusicPlayerHelper.playingMusic
        if (init) {
            if (music == null) {
                stopSelf()
                return
            }
            sendForegroundNotify(music, progress, isPlaying)
        } else {
            if (music == null) {
                stopForeground(STOP_FOREGROUND_REMOVE)
                return
            }
            sendNotify(music, progress, isPlaying)
        }
    }

    private fun sendForegroundNotify(music: MusicInfo, progress: Long, isPlaying: Boolean) {
        val channel = createChannel()
        if (channel != null) {
            val manager = NotificationManagerCompat.from(this)
            manager.createNotificationChannel(channel)
        }
        createNotify(music, progress, isPlaying) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    musicPlayerNotifyId, it, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                )
            } else {
                startForeground(musicPlayerNotifyId, it)
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun sendNotify(music: MusicInfo, progress: Long, isPlaying: Boolean) {
        Log.d("MusicPlayService", "MissingPermission  sendNotify")
        createNotify(music, progress, isPlaying) {
            val channel = createChannel()
            val manager = NotificationManagerCompat.from(this)
            if (channel != null) {
                manager.createNotificationChannel(channel)
            }
            manager.notify(musicPlayerNotifyId, it)
                Log.d("MusicPlayService", "notify")
        }
    }


    private fun createChannel(): NotificationChannel? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                musicPlayerChannel,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.enableLights(false)
            channel.enableVibration(false)
            channel.setSound(null, null)
            channel
        } else {
            null
        }
    }

    private fun createNotify(
        music: MusicInfo,
        progress: Long,
        isPlaying: Boolean,
        complete: (Notification) -> Unit
    ) {
        createRemoteView(music, progress, isPlaying) { smallRemoteViews ->
            val builder = NotificationCompat.Builder(this, musicPlayerChannel)
            builder.setSmallIcon(R.mipmap.ic_launcher)
            builder.setCustomContentView(smallRemoteViews)
            builder.setContent(smallRemoteViews)

            builder.priority = NotificationCompat.PRIORITY_HIGH
            builder.setAutoCancel(false)
            builder.setOngoing(true)
            builder.setSilent(true)
            builder.setOnlyAlertOnce(true)
            builder.setVibrate(null)
            builder.setSound(null)
            complete.invoke(builder.build())
        }
    }

    private fun createRemoteView(
        music: MusicInfo,
        progress: Long,
        isPlaying: Boolean,
        complete: (RemoteViews) -> Unit
    ) {
        val remoteViews1 = RemoteViews(packageName, R.layout.layout_notify_play)
        TB.scope.launch {
            val name = music.displayName
            var singer = music.singerName
            if (singer.isEmpty()) {
                singer = getString(R.string.unknown)
            }
            val isCollection = MusicDataHelper.isCollect(music)
            withContext(Dispatchers.Main) {
                setRemoteViews(
                    remoteViews1,
                    name,
                    singer,
                    progress,
                    music.duration,
                    isCollection,
                    true,
                    isPlaying
                )
                complete.invoke(remoteViews1)
            }
        }
    }

    private fun setRemoteViews(
        remoteViews: RemoteViews,
        name: String,
        singer: String,
        progress: Long,
        duration: Long,
        isCollection: Boolean,
        isSetCollection: Boolean,
        isPlaying: Boolean
    ) {
        remoteViews.setTextViewText(R.id.name, name)
        remoteViews.setTextViewText(R.id.desc, singer)
        remoteViews.setImageViewResource(
            R.id.play_image,
            if (isPlaying) R.drawable.img_bottom_music_start else R.drawable.img_bottom_music_stop
        )

        remoteViews.setOnClickPendingIntent(R.id.vibe_root_layout, createActivityPending())
        remoteViews.setOnClickPendingIntent(
            R.id.next_image, createReceiverPending(MusicPlayerHelper.Action.ACTION_NEXT_SONG)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.play_image, createReceiverPending(MusicPlayerHelper.Action.ACTION_PLAY_PAUSE)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.previous_image, createReceiverPending(MusicPlayerHelper.Action.ACTION_PREVIOUS)
        )
    }

    private fun createReceiverPending(action: String): PendingIntent {
        return PendingIntent.getBroadcast(
            this, receiverCode++, Intent(action).apply {
                `package` = packageName
            }, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
    }

    private fun createActivityPending(): PendingIntent {
        return PendingIntent.getActivity(
            this, activityCode++, Intent(this, MainActivity::class.java).apply {
                `package` = packageName
                putExtra("start_from", "play_notify")
            }, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
    }



}