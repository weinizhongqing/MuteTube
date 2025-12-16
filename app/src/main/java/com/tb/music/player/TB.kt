package com.tb.music.player

import android.app.Application
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import coil.Coil
import coil.ImageLoader
import com.google.firebase.Firebase
import com.tb.music.player.config.AppConfig
import com.tb.music.player.config.ConfigManager
import com.tb.music.player.config.MuteRemoteConfig
import com.tb.music.player.music.EqualizerManager
import com.tb.music.player.music.MusicPlayerHelper
import com.tb.music.player.music.db.MusicDataHelper
import com.tb.music.player.music.equalizer.EqualizerHelper
import com.tb.music.player.tobe.InitToBe
import com.tb.music.player.utils.LanguageUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class TB  : Application(){

    companion object{
        lateinit var instance: TB
            private set

        val scope by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler { _, throwable ->
                throwable.printStackTrace()
                Firebase.crashlytics.recordException(throwable)
            })
        }

        const val solarKey = "988ede2fd99bc117"

        val FCM_URL: String
            get() {
                return if (BuildConfig.DEBUG) {
                    "https://test.cloudbili.xyz/vd/cok/"
                } else {
                    "https://prod.cloudbili.xyz/vd/cok/"
                }
            }

        val CLOCK_URL: String
            get() = "https://prod.cloudbili.xyz/vd/ka/"

        var ignoreStart = false

    }

    val isBuy : Boolean
        get() {
            if (BuildConfig.DEBUG){
                return true
            }
            return MuteRemoteConfig.ins.isBuy
        }


    override fun onCreate() {
        super.onCreate()
        instance  = this
        AppConfig.initMMKV()
        LanguageUtil.init()
       // InitToBe.initToBe(this)
        ConfigManager.init()
        ConfigManager.reportToken()
        initMediaPlayer()
        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .crossfade(true)
                .allowHardware(false)
                .build()
        )
        AppForegroundState.register(this)
    }


    private fun initMediaPlayer() {
        MusicPlayerHelper.initExoPlayer()
    }

}