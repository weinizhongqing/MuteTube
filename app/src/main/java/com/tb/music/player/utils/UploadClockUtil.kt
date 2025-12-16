package com.tb.music.player.utils

import android.os.Bundle
import com.tb.music.player.BuildConfig
import com.tb.music.player.TB
import com.tb.music.player.config.AppConfig
import com.tb.music.player.config.MuteEvent
import com.tb.music.player.config.MuteRemoteConfig
import kotlinx.coroutines.launch
import okhttp3.Request
import kotlin.code

object UploadClockUtil {


    fun uploadClock() {
        if (!MuteRemoteConfig.ins.isUploadClock || AppConfig.isUploadPassClock) return
        MuteEvent.event("upload_clock_start")
        TB.scope.launch {
            val request = Request.Builder()
                .url(TB.CLOCK_URL)
                .header("VD", TB.instance.packageName)
                .header("ESW", BuildConfig.VERSION_NAME)
                .build()

            try {
                val response = NetClientUtil.getUnsafeOkHttpClient().newCall(request).execute()
                if (response.isSuccessful) {
                    AppConfig.isUploadPassClock = true
                    MuteEvent.event("upload_clock_success")
                } else {
                    MuteEvent.event("upload_clock_failed", Bundle().apply {
                        putString("failed", response.code.toString())
                    })
                }
            } catch (e: Exception) {
                e.printStackTrace()
                MuteEvent.event("upload_clock_error", Bundle().apply {
                    putString("error", e.message)
                })
            }

        }
    }

}