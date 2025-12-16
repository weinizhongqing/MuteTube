package com.tb.music.player

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

object AppForegroundState {

    var isForeground by mutableStateOf(true)
        private set

    // 👇 是否真正进入过后台
    var hasEnteredBackground = false
        private set

    fun register(application: Application) {
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            object : DefaultLifecycleObserver {

                override fun onStart(owner: LifecycleOwner) {
                    isForeground = true
                }

                override fun onStop(owner: LifecycleOwner) {
                    isForeground = false
                    hasEnteredBackground = true
                }
            }
        )
    }
}
