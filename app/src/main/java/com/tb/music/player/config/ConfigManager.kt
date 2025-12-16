package com.tb.music.player.config

import android.util.Base64
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.FirebaseApp
import com.reyun.solar.engine.OnAttributionListener
import com.reyun.solar.engine.SolarEngineConfig
import com.reyun.solar.engine.SolarEngineManager
import com.reyun.solar.engine.infos.SEAdImpEventModel
import com.tb.music.player.MarkHub
import com.tb.music.player.TB
import com.tb.music.player.music.db.MusicData
import com.tb.music.player.music.db.info.AdPaidData
import com.tb.music.player.notify.StreamHandler
import com.tb.music.player.pus.PusManager
import kotlinx.coroutines.launch
import org.json.JSONObject

object ConfigManager {

    private data class FaceBookInfo(val id: String, val token: String)

    private var cachedFaceBookInfo: FaceBookInfo? = null
    private var lastInitFacebookId: String? = null


    fun init() {
        try {
            FirebaseApp.initializeApp(TB.instance)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            PusManager.instance.initialize(TB.instance)
        } catch (_: Exception) {

        }
        initRef()
        initFacebook()
        initSolar()
    }

    fun reinitChannel() {
        initFacebook()
        initSolar()
    }

    /** 初始化 Facebook SDK */
    private fun initFacebook() {
        val info = getFBInfo() ?: return
        try {
            FacebookSdk.setClientToken(info.token)
            FacebookSdk.setApplicationId(info.id)
            FacebookSdk.sdkInitialize(TB.instance)
            AppEventsLogger.activateApp(TB.instance)
            MuteEvent.uploadSaveAdValueToFb()
            MuteEvent.uploadSaveClickAdValueToFb()
//            Log.d("ConfigManager", "Facebook SDK initialized.")
        } catch (e: Exception) {
//            Log.e("ConfigManager", "Facebook init failed", e)
        }
    }

    private fun initRef() {
        MarkHub.ins.init(TB.instance)
    }

    fun reportToken() {
        StreamHandler.instance.reportToken()
    }

    /** 初始化 Solar 引擎 */
    private fun initSolar() {
        val fbId = getFBInfo()?.id ?: return
        if (fbId ==lastInitFacebookId) return

        lastInitFacebookId = fbId

        val appKey = TB.solarKey
        SolarEngineManager.getInstance().preInit(TB.instance, appKey)

        val config = SolarEngineConfig.Builder().setFbAppID(fbId).build()
        config.setOnAttributionListener(object : OnAttributionListener {
            override fun onAttributionSuccess(attribution: JSONObject) {
                val channel = attribution.optString("channel_id")
                if (channel.isNotEmpty()) {
                    AppConfig.reyunRefStr = channel
                    if (AppConfig.reyunMl(channel)) {
                        AppConfig.userKey = "tb_user_v"
                        MuteEvent.event("tb_sf_m")
                    }
                }
            }

            override fun onAttributionFail(errorCode: Int) {
//                Log.e("ConfigManager", "Attribution failed, code=$errorCode")
            }
        })

        SolarEngineManager.getInstance()
            .initialize(TB.instance, appKey, config) { code ->
                if (code == 0) {
//                Log.d("ConfigManager", "Solar init success")
                    TB.scope.launch {
                        val adValueModel =  MusicData.data.adPaidData().getAlls()
                        if (adValueModel.isEmpty()){
                            return@launch
                        }
                        adValueModel.forEach { it ->
                            upLoadReYun(it)
                            MusicData.data.adPaidData().delete(it)
                        }
                    }
                } else {
//                Log.e("ConfigManager", "Solar init failed, code=$code")
                    MuteEvent.event("tb_solar_init_fail", "msg", code.toString())
                }
            }
    }

    /** 解析并缓存 FBInfo */
    private fun getFBInfo(): FaceBookInfo? {
        if (cachedFaceBookInfo != null) return cachedFaceBookInfo

        return try {
            val raw = MuteRemoteConfig.ins.getFBInfo()
            if (raw.isEmpty()) return null

            val json = JSONObject(String(Base64.decode(raw, Base64.NO_WRAP)))
            val id = json.optString("tb_fb_id")
            val token = json.optString("tb_fb_token")

            if (id.isNotEmpty() && token.isNotEmpty()) {
                FaceBookInfo(id, token).also { cachedFaceBookInfo = it }
            } else null
        } catch (e: Exception) {
//            Log.e("ConfigManager", "Failed to parse fbInfo", e)
            null
        }
    }

    /** 上报缓存的 Reyun 事件 */
    private fun upLoadReYun(adPaidData: AdPaidData){
        val seAdImpEventModel = SEAdImpEventModel(
            adPaidData.source,
            adPaidData.platform,
            adPaidData.adType,
            adPaidData.adFormat,
            adPaidData.adUnitId,
            adPaidData.value,
            adPaidData.currency,
            true,
            JSONObject()
        )
        SolarEngineManager.getInstance().trackAdImpression(seAdImpEventModel)
    }
    
}