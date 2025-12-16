package com.tb.music.player.music

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import com.tb.music.player.TB
import com.tb.music.player.config.AppConfig
import com.tb.music.player.music.callback.OnPlayerCallback
import com.tb.music.player.music.db.MusicDataHelper
import com.tb.music.player.music.db.info.MusicInfo
import com.tb.music.player.music.receiver.TBPlayReceiver
import com.tb.music.player.music.service.MusicPlayService
import com.tb.music.player.music.status.LoadStatus
import com.tb.music.player.music.status.PlayState
import com.tb.music.player.music.status.PlayStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.max
import kotlin.math.min

object MusicPlayerHelper : OnPlayerCallback {

    private lateinit var playerManager : ExoPlayerManager


    private val _playProgress = MutableLiveData<Pair<Long, Long>>()
    val playProgress: LiveData<Pair<Long, Long>> = _playProgress

    private val _playMusic = MutableLiveData<MusicInfo?>()
    val playMusic: LiveData<MusicInfo?> = _playMusic

    private val _playState = MutableLiveData<PlayStatus>()
    val playState: LiveData<PlayStatus> = _playState

    private val _loadState = MutableLiveData<LoadStatus>()
    val loadState: LiveData<LoadStatus> = _loadState

    private val _playMusicList = MutableLiveData<MutableList<MusicInfo>>()
    val playMusicList: LiveData<MutableList<MusicInfo>> = _playMusicList

    private val _collection = MutableLiveData<Boolean>()
    val collection: LiveData<Boolean> = _collection

    val isPlaying: Boolean
        get() {
            return _playState.value == PlayStatus.PLAYING
        }

    val playMusicProgress: Long
        get() {
            return _playProgress.value?.second ?: 0
        }

    val playMusicDuration: Long
        get() {
            return _playProgress.value?.first ?: 0
        }

    val isCollection: Boolean
        get() {
            return _collection.value ?: false
        }

    val playingMusic: MusicInfo?
        get() {
            return _playMusic.value
        }

    private var playingListIndex = 0

    private var savePlayState: PlayStatus? = null

    val equalizerManager by lazy { EqualizerManager() }

    val playerId: Int
        @SuppressLint("UnsafeOptInUsageError")
        get() {
            return playerManager.player.audioSessionId
        }

    fun initExoPlayer(){
        playerManager = ExoPlayerManager(this)
        registerPlayBroadcast()
        MusicDataHelper.addDefaultPlayList()
       // initPlayList()
    }


    fun savePlayState() {
        savePlayState = _playState.value
        if (savePlayState == PlayStatus.PLAYING) {
            TB.instance.sendBroadcast(Intent(Action.ACTION_PLAY_PAUSE).apply {
                `package` =  TB.instance.packageName
            })
        }
    }

    fun restorePlayState() {
        if (savePlayState == null) return
        if (savePlayState == PlayStatus.PLAYING) {
            TB.instance.sendBroadcast(Intent(Action.ACTION_PLAY_PAUSE).apply {
                `package` =  TB.instance.packageName
            })
        }
        savePlayState = null
    }


    fun seekTo(time: Long) {
        playerManager.player.seekTo(time)
        setPlayProgress(time, playingMusic)
    }

    private fun setPlayProgress(progress: Long, music: MusicInfo?) {
        if ((playProgress.value?.second ?: -1) == progress) return
        _playProgress.postValue(Pair(music?.duration ?: 0, progress))
    }

    fun switchPlayState() {
        if (playerManager.player.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        if (playerManager.player.isPlaying) return
        if (_playState.value != PlayStatus.PLAYING) {
            _playState.postValue(PlayStatus.PLAYING)
        }
        playerManager.player.play()
        setShutdown(false)
    }


    fun addToRecent() {
        playingMusic?.let {
            MusicDataHelper.addMusicToPlayList(
                MusicDataHelper.recentlyListId,
                it
            ) {}
        }
    }


    private var shutdownJob: Job? = null

    fun setShutdown(isCancel: Boolean = false) {
        shutdownJob?.cancel()
        if (isCancel) return
        val time = AppConfig.stopTime
        if (time <= 0) return
        //Log.d("setShutdown", "start time: $time")
        shutdownJob = TB.scope.launch {
            delay(time * DateUtils.MINUTE_IN_MILLIS)
            //Log.d("setShutdown", "stop+++")
            if (isPlaying) TB.instance.sendBroadcast(Intent(Action.ACTION_PLAY_PAUSE).apply {
                `package` = TB.instance.packageName
            })
        }
    }

    fun addMusic(music: MusicInfo) {
        val currentMusicList = _playMusicList.value
        val isStart = currentMusicList.isNullOrEmpty()
        var playList: MutableList<MusicInfo>? = null
        if (currentMusicList != null) {
            for (mus in currentMusicList) {
                if (music.songId == mus.songId) {
                    return
                }
            }
            if (playList == null) {
                playList = mutableListOf()
                playList.addAll(currentMusicList)
            }
            playList.add(music)
        } else {
            playList = mutableListOf(music)
        }
        if (isStart) {
            setPlayingMusic(music)
            playMusic(music, false)
        }
        _playMusicList.postValue(playList!!)
    }

    fun deleteMusic(music: MusicInfo) {
        val currentMusicList = _playMusicList.value
        if (currentMusicList != null) {
            val iterator = currentMusicList.iterator()
            while (iterator.hasNext()) {
                if (iterator.next().songId == music.songId) {
                    iterator.remove()
                    break
                }
            }
            _playMusicList.postValue(currentMusicList!!)
            if (music.songId == playingMusic?.songId) {
                if (currentMusicList.isEmpty()) {
                    setPlayingMusic(null)
                    pause()
                } else {
                    TB.instance.sendBroadcast(Intent(Action.ACTION_NEXT_SONG).apply {
                        `package` = TB.instance.packageName
                    })
                }
            }
        }
    }

    fun deleteAllMusic() {
        pause()
        setPlayingMusic(null)
        _playMusicList.postValue(mutableListOf())
        playerManager.player.clearMediaItems()
    }

    fun collectChange(songId: Long) {
        if (playingMusic?.songId == songId || songId == -1L) {
            TB.scope.launch {
                val isCollection = if (playingMusic != null) {
                    MusicDataHelper.isCollect(playingMusic!!)
                } else {
                    false
                }
                _collection.postValue(isCollection)
            }
        }
    }

    fun clearPlayList(){
        _playMusicList.postValue(mutableListOf())
    }


    fun addMusicToPlayList(music: MusicInfo) {
        _playMusicList.postValue(mutableListOf(music))
    }

    fun addMusicToPlayList(musicList: MutableList<MusicInfo>) {
        _playMusicList.postValue(musicList)
    }



    fun isPlayingMusic(music: MusicInfo): Boolean {
        if (playingMusic == null) return false
        return music.songId == playingMusic!!.songId
    }

    fun initPlayList() {
        val playListId = AppConfig.playListId
        if (playListId < 0) return
        equalizerManager.setUsePreset(AppConfig.selectPresetIndex)
        MusicDataHelper.getPlayListMusic(playListId) { musicList ->
            if (musicList.isEmpty()) return@getPlayListMusic
            val currentMusicId = AppConfig.playMusicId
            var index = 0
            for (i in musicList.indices) {
                if (currentMusicId == musicList[i].songId) {
                    index = i
                    break
                }
            }
            setPlayList(playListId, index, musicList, isPlay = false, isInit = true)
        }
    }

    fun setPlayList(
        listId: Long,
        index: Int,
        musicList: MutableList<MusicInfo>,
        isPlay: Boolean,
        isInit: Boolean = false
    ) {
        if (listId == AppConfig.playListId && !isInit) {
            val playIndex = min(max(index, 0), musicList.size - 1)
            playingListIndex = playIndex
            val music = musicList[playIndex]
            if (music.songId != playingMusic?.songId) {
                setPlayingMusic(music)
                playMusic(music, isPlay)
            } else {
                play()
            }
        } else {
            AppConfig.playListId = listId
            val playIndex = min(max(index, 0), musicList.size - 1)
            playingListIndex = playIndex
            _playMusicList.postValue(musicList)
            musicList[playIndex].apply {
                setPlayingMusic(this)
                playMusic(this, isPlay)
            }
        }
    }

    fun playNext() {
        playNext(0)
    }

    fun playPrevious() {
        getPlayMusic(false)?.let {
            playMusic(it, isPlaying)
        }
    }

    private fun playNext(type: Int) {
        if (type == 1) {
            if (AppConfig.playMode == PlayState.SINGLE) {
                seekTo(0)
                return
            }
        }
        getPlayMusic(true)?.let {
            playMusic(it, isPlaying)
        }
    }

    private fun getPlayMusic(isNext: Boolean): MusicInfo? {
        val musicList = _playMusicList.value
        if (musicList.isNullOrEmpty()) {
            setPlayingMusic(null)
            return null
        }
        when (AppConfig.playMode) {
            PlayState.LIST, PlayState.SINGLE -> {
                if (isNext) {
                    playingListIndex++
                } else {
                    playingListIndex--
                }
                if (playingListIndex >= musicList.size) {
                    playingListIndex = 0
                } else if (playingListIndex < 0) {
                    playingListIndex = musicList.size - 1
                }
            }

            PlayState.RANDOM -> {
                playingListIndex = Random().nextInt(musicList.size)
            }
        }
        val music = musicList[playingListIndex]
        setPlayingMusic(music)
        return music
    }
    fun playMusic(music: MusicInfo, isPlay: Boolean) {
        _playMusic.postValue(music)

        // 暂停之前播放的音乐
        if (playerManager.player.isPlaying) playerManager.player.pause()

        // 设置 MediaItem
        playerManager.player.setMediaItem(MediaItem.fromUri(music.path))
        playerManager.player.prepare()

        // 使用 playWhenReady 确保首次播放不会错过 audioSessionId
        playerManager.player.playWhenReady = isPlay

        // 更新状态
        if (isPlay) {
            if (_playState.value != PlayStatus.PLAYING) {
                _playState.postValue(PlayStatus.PLAYING)
            }
            setShutdown(false)
        }
    }


    private fun pause() {
        if (!playerManager.player.isPlaying) return
        if (_playState.value != PlayStatus.PAUSE) {
            _playState.postValue(PlayStatus.PAUSE)
        }
        playerManager.player.pause()
        setShutdown(true)
    }

    private fun setPlayingMusic(music: MusicInfo?) {
        AppConfig.playMusicId = music?.songId ?: -1
        _playMusic.postValue(music)
        setPlayProgress(0, music)
        if (music == null) {
            setPlayState(PlayStatus.NONE)
            setLoadState(LoadStatus.LOADING)
            playingListIndex = 0
        }
        collectChange(-1)
        if (music != null) {
            MusicPlayService.startPlayService()
        }
    }

    private fun setPlayState(state: PlayStatus) {
        if (state == _playState.value) return
        _playState.postValue(state)
    }

    private fun setLoadState(state: LoadStatus) {
        if (state == _loadState.value) return
        _loadState.postValue(state)
    }

    override fun onPlayNextMusic(type: Int) {

        playNext(type)
    }

    override fun onPlayMusicFail() {
        play()
    }

    override fun onLoadMusicStateChange(state: LoadStatus) {
        setLoadState(state)
    }

    override fun onPlayMusicProgressChange(progress: Long) {
        setPlayProgress(progress, playingMusic)
    }

    fun registerPlayBroadcast() {
        TB.instance.registerBroadcast(TBPlayReceiver(), IntentFilter().apply {
            addAction(Action.ACTION_PREVIOUS)
            addAction(Action.ACTION_COLLECTION_CLICK)
            addAction(Action.ACTION_NEXT_SONG)
            addAction(Action.ACTION_PLAY_PAUSE)
            addAction(MusicDataHelper.Action.ACTION_COLLECT_CHANGE)
        })
    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun Context.registerBroadcast(receiver: BroadcastReceiver, intentFilter: IntentFilter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(receiver, intentFilter)
        }
    }

    object Action {
        const val ACTION_NEXT_SONG = "com.tb.pl.music.NEXT_SONG"
        const val ACTION_PREVIOUS = "com.tb.pl.music.PREVIOUS"
        const val ACTION_PLAY_PAUSE = "com.tb.pl.music.PLAY_PAUSE"
        const val ACTION_COLLECTION_CLICK = "com.tb.pl.music.COLLECTION_CLICK"
    }
}