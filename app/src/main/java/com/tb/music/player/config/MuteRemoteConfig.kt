package com.tb.music.player.config

import android.util.Base64
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.tb.music.player.R
import com.tb.music.player.pus.PusManager
import com.tb.music.player.utils.UploadClockUtil
import kotlin.text.ifEmpty

class MuteRemoteConfig {

    private val config: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    companion object {
        val ins: MuteRemoteConfig by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { MuteRemoteConfig() }
    }

    private var isComplete = false

    init {
        config.setDefaultsAsync(R.xml.firebase_remote_config)
        config.fetchAndActivate().addOnCompleteListener {
            isComplete = it.isSuccessful
            if (it.isSuccessful){
                remoteConfigUpdate()
            }
        }

        config.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                config.activate().addOnCompleteListener {
                    remoteConfigUpdate()
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
//                Log.w(TAG, "Config update error with code: " + error.code, error)
            }
        })
    }



    private fun remoteConfigUpdate() {
        PusManager.instance.initCfg()
        ConfigManager.reinitChannel()
        ConfigManager.reportToken()
        UploadClockUtil.uploadClock()
    }



    fun getAdConfig() = config.getString("tb_aa_cfg")


    //ewoidGJfZmJfaWQiOiI0NzkxMTc0MjQ5MDQxODAiLAoidGJfZmJfdG9rZW4iOiJlOWY3MmFkNzdmMWRhYmEwY2Q1NjAzZmE0YjJmYjJkNiIKfQ==
    fun getFBInfo() = config.getString("tb_fb_cfg")


    val updateFbAdValue: Float
        get() {
            val value = config.getLong("tb_fb_v_coefficient")
            if (value <= 0) return 1f
            return value / 100f
        }


    fun getFBThreshold() = config.getDouble("tb_fb_threshold")


    fun getUseConfig() = config.getString("tb_ut_config")


    fun getExpLink(): String {
        val link = config.getString("tb_def_link")
            .ifEmpty { "aHR0cHM6Ly93d3cudGlrdG9rLmNvbS9AZGlhbm5haC5lL3ZpZGVvLzc1NjYyMjYzNzE3NDgzNTEyNTM=" }
        return String(Base64.decode(link, Base64.NO_WRAP))
    }


    // VIP 试用小时数
    val vipHour: Long
        get() = config.getLong("tb_cfg_v_hour").takeIf { it > 0 } ?: 48

    // 是否付费用户
    val isBuy: Boolean
        get() = config.getBoolean(AppConfig.userKey.orEmpty().ifEmpty { "tb_user_p" })

    val isUploadClock : Boolean
        get() {
            return config.getBoolean("tb_is_upload_clock")
        }

    val fbBoundary: Double
        get() = config.getDouble("tb_fb_boundary")

    val isShowCountdown: Boolean
        get() = config.getBoolean("tb_is_full_screen_native_countdown")

    val fullScreenNativeCountdownTime: Long
        get() = config.getLong("tb_full_screen_native_countdown_time")

}