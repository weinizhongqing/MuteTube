package com.tb.music.player.notify

import android.util.Log
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.messaging.FirebaseMessaging
import com.tb.music.player.TB
import com.tb.music.player.config.AppConfig
import com.tb.music.player.config.MuteEvent
import com.tb.music.player.utils.NetClientUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class StreamHandler private constructor() {

    companion object {
        private const val TAG = "tb_FCM"
        val instance: StreamHandler by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { StreamHandler() }
        private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    }

    private val client = NetClientUtil.getUnsafeOkHttpClient()
    private val uploadMutex = Mutex()
    private var uploadTokenJob: Job? = null

    fun reportToken() {
        if (uploadTokenJob?.isActive == true) return

        uploadTokenJob = scope.launch {
            uploadMutex.withLock {
                val token = getToken() ?: return@launch
                val lastToken = AppConfig.getToken()

                if (token == lastToken && AppConfig.getRpFcmDally()) return@launch

                MuteEvent.event("tb_tkup_st")

                var gaid = AppConfig.getGaid()
                if (gaid.isEmpty()) {
                    gaid = getGoogleAdvertisingId()
                    AppConfig.setGaid(gaid)
                }

                val payloadJson = JSONObject().apply {
                    put("water", gaid)
                    put("study", AppConfig.appFirstOpenTime.toString())
                    put("music", token)
                    put("swiftref", AppConfig.plyRefStr)
                }

                val request = Request.Builder()
                    .url(TB.FCM_URL)
                    .post(payloadJson.toString().toRequestBody("application/json".toMediaType()))
                    .addHeader("VD", TB.instance.packageName)
                    .addHeader("ESW", getAppVersionName())
                    .addHeader("Content-Type", "application/json")
                    .build()

                logRequest(request, payloadJson)

                var attempts = 0
                var success = false
                var lastError = ""

                while (attempts++ < 3 && !success) {
                    val response = try {
                        client.newCall(request).execute()
                    } catch (e: Exception) {
                        lastError = e.message ?: "Unknown error"
                        null
                    }

                    if (response?.isSuccessful == true) {
                        val responseBody = response.body?.string()
                        logD("Upload token successful: $responseBody")
                        AppConfig.setRpFcmDally()
                        AppConfig.setToken(token)
                        MuteEvent.event("tb_tkup_succ")
                        success = true
                    } else {
                        lastError = "${response?.code} -> ${response?.message}"
                        if (attempts < 3) delay(10_000)
                    }
                }

                if (!success) {
                    logE("Upload token failed after 3 attempts: $lastError")
                    MuteEvent.event("tb_tkup_fail", "msg", lastError)
                }
            }
        }
    }

    private suspend fun getToken(): String? = suspendCoroutine { cont ->
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            cont.resume(if (it.isSuccessful) it.result else null)
        }
    }

    private suspend fun getGoogleAdvertisingId(): String {
        return try {
            val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(TB.instance)
            logD("Advertising ID: ${adInfo.id}")
            adInfo.id ?: ""
        } catch (e: Exception) {
            logE("Error getting GAID", e)
            ""
        }
    }

    private fun getAppVersionName(): String {
        return try {
            val packageInfo = TB.instance.packageManager.getPackageInfo(TB.instance.packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            logE("Failed to get version name", e)
            "1.0.0"
        }
    }

    private fun logRequest(request: Request, body: JSONObject) {
        logD("Request URL: ${request.url}")
        request.headers.forEach { logD("Header: ${it.first} = ${it.second}") }
        logD("Request Body:\n${body.toString(3)}")
    }

    private fun logD(msg: String) = Log.d(TAG, msg)
    private fun logE(msg: String, e: Throwable? = null) {
        if (e != null) Log.e(TAG, msg, e) else Log.e(TAG, msg)
    }
}