package com.tb.music.player

import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.reyun.solar.engine.SolarEngineManager
import com.tb.music.player.config.AppConfig
import com.tb.music.player.config.MuteEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class MarkHub {

    private lateinit var client: InstallReferrerClient

    companion object {
        val ins: MarkHub by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { MarkHub() }
    }

    private var isFetchingReferrer = false

    fun init(context: Context) {
        TB.scope.launch {
            getReferrer(context)
            getSolarData()
            MuteEvent.init()
        }
    }

    /**
     * 获取安装引用信息 (Install Referrer)
     */
    private suspend fun getReferrer(context: Context): Boolean = suspendCancellableCoroutine { cont ->
        if (!AppConfig.plyRefStr.isNullOrEmpty()) {
            cont.resume(false)
            return@suspendCancellableCoroutine
        }

        MuteEvent.event("tb_f_sta")

        if (isFetchingReferrer) {
            cont.resume(true)
            return@suspendCancellableCoroutine
        }

        isFetchingReferrer = true

        client = InstallReferrerClient.newBuilder(context).build()

        client.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                try {
                    if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK && client.isReady) {
                        val response = client.installReferrer
                        val referrerUrl = response.installReferrer
                        if (referrerUrl.isNotEmpty()) {
                            AppConfig.plyRefStr = referrerUrl
                            if (AppConfig.isM()) {
                                AppConfig.userKey = "tb_user_v"
                                MuteEvent.event("tb_f_s")
                            } else {
                                MuteEvent.event("tb_f_p")
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    client.endConnection()
                    isFetchingReferrer = false
                    if (cont.isActive) cont.resume(true)
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                isFetchingReferrer = false
                if (cont.isActive) cont.resume(true)
            }
        })

        cont.invokeOnCancellation {
            // 取消协程时断开连接，避免泄露
            if (::client.isInitialized) {
                try {
                    client.endConnection()
                } catch (_: Exception) { }
            }
            isFetchingReferrer = false
        }
    }

    /**
     * 延迟获取Solar数据，模拟网络请求或异步任务
     */
    private suspend fun getSolarData(): Boolean {
        delay(5000)
        MuteEvent.event("tb_rl_start")

        val success = getSolarValueWithRetry(20)

        if (AppConfig.reyunMl()) {
            MuteEvent.event("tb_rl_m")
            AppConfig.userKey = "tb_user_v"
        } else {
            MuteEvent.event("tb_rl_o")
        }

        return success
    }

    /**
     * 递归重试获取Solar渠道数据
     */
    private tailrec suspend fun getSolarValueWithRetry(retriesLeft: Int): Boolean {
        if (!AppConfig.reyunRefStr.isNullOrEmpty()) {
            return true
        }

        val attribution = SolarEngineManager.getInstance().attribution
        val channel = attribution?.optString("channel_id").orEmpty()

        return if (channel.isNotEmpty()) {
            AppConfig.reyunRefStr = channel
            true
        } else if (retriesLeft > 0) {
            delay(3000)
            getSolarValueWithRetry(retriesLeft - 1)
        } else {
            false
        }
    }
}