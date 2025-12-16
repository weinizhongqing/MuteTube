package com.tb.music.player.config

import android.os.Bundle
import android.util.Log
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.ResponseInfo
import com.google.firebase.analytics.FirebaseAnalytics
import com.reyun.solar.engine.SolarEngineManager
import com.reyun.solar.engine.infos.SEAdImpEventModel
import com.tb.music.player.TB
import com.tb.music.player.music.db.MusicData
import com.tb.music.player.music.db.info.AdPaidData
import com.tb.music.player.pus.Type
import org.json.JSONObject
import java.math.BigDecimal
import java.util.Currency

object MuteEvent {

    private const val TAG = "tb_analytics"

    private val analytics = FirebaseAnalytics.getInstance(TB.instance)

    fun init() {
        analytics.setUserProperty("tb_user", if (TB.instance.isBuy) "sv" else "sp")
        //Log.i(TAG,"setUserProperty->skit_user,${if (VdConfig.isMp) "vd" else "nn"}")
        analytics.setUserProperty("tb_day", AppConfig.getInstallDay())
        //Log.i(TAG,"setUserProperty->yhlc,${DfConfig.ins.getInstallDay()}")
    }

    fun event(action: String, key: String, value: String) {
        val bundle = Bundle()
        bundle.putString(key, value)
        analytics.logEvent(action, bundle)
        Log.i(TAG, "logEvent->${action} [$key=$value]")
    }

    fun event(action: String, bundle: Bundle? = null) {
        analytics.logEvent(action, bundle)
        Log.i(TAG, "logEvent->${action},${bundle ?: ""}")
    }


    fun logEvent(
        key: String,
        adType: Type,
        adValue: AdValue,
        responseInfo: ResponseInfo?,
        id: String
    ) {
        val value = adValue.valueMicros.toDouble() / 1_000_000.0
        val network = responseInfo?.mediationAdapterClassName ?: ""

        dealSolarValue(adValue, responseInfo, id, adType)
        dealValueImpressionFb(adValue)

        logFirebaseEvent("tb_ad_revalue", value, adValue, network, id)
        logFirebaseEvent("tb_inc_rev_$key", value, adValue, network, id)
        if (value > 0.01){
            logFirebaseEvent("tb_rev_t_001", value, adValue, network, id)
        }
    }


    private fun logFirebaseEvent(
        eventName: String,
        value: Double,
        adValue: AdValue,
        network: String,
        adUnitId: String
    ) {
        event(eventName, Bundle().apply {
            putDouble(FirebaseAnalytics.Param.VALUE, value)
            putString(FirebaseAnalytics.Param.CURRENCY, adValue.currencyCode)
            putString("precision", adValue.precisionType.toString())
            putString("network", network)
            putString("adunitid", adUnitId)
        })
    }

    private fun dealValueImpressionFb(adValue: AdValue) {
        val value = adValue.valueMicros.toDouble() / 1_000_000.0
        if (value< MuteRemoteConfig.ins.fbBoundary){
            return
        }
        if (FacebookSdk.isInitialized()) {
            val newVal = value * MuteRemoteConfig.ins.updateFbAdValue
            val logger = AppEventsLogger.newLogger(TB.instance)
            logger.logPurchase(
                BigDecimal.valueOf(newVal), Currency.getInstance(adValue.currencyCode)
            )
            logger.logEvent(
                AppEventsConstants.EVENT_NAME_AD_IMPRESSION,
                newVal,
                Bundle().apply {
                    putString(AppEventsConstants.EVENT_PARAM_CURRENCY, adValue.currencyCode)
                })
        } else {
            AppConfig.run {
                showAdValue += value
                showAdValueUnit = adValue.currencyCode
            }
        }
        if (!AppConfig.uploadAdCumValue) {
            AppConfig.showAdSaveValue += value
            val facebookUploadValue = MuteRemoteConfig.ins.getFBThreshold()
            if (facebookUploadValue > 0 && AppConfig.showAdSaveValue >= facebookUploadValue && FacebookSdk.isInitialized()) {
                AppConfig.uploadAdCumValue = true
                val parameters = Bundle()
                parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, adValue.currencyCode)
                AppEventsLogger.newLogger(TB.instance)
                    .logEvent("tb_meet_fb_val", AppConfig.showAdSaveValue, parameters)
            }
        }
    }

    fun dealValueClickFb(adValue: AdValue?) {
        adValue?.let {
            val value = adValue.valueMicros.toDouble() / 1_000_000.0
            if (value < MuteRemoteConfig.ins.fbBoundary){
                return
            }
            if (FacebookSdk.isInitialized()) {
                val logger = AppEventsLogger.newLogger(TB.instance)
                logger.logEvent(AppEventsConstants.EVENT_NAME_AD_CLICK, value, Bundle().apply {
                    putString(AppEventsConstants.EVENT_PARAM_CURRENCY, it.currencyCode)
                })
            } else {
                AppConfig.run {
                    clickAdValue += value
                    clickAdValueUnit = it.currencyCode
                }
            }
        }
    }


    private fun dealSolarValue(
        adValue: AdValue,
        responseInfo: ResponseInfo?,
        id: String,
        adType: Type
    ) {

        Log.d(TAG,"dealSolarValue->${adValue.toString()}")
        Log.d(TAG,"dealSolarValue->${id.toString()}")
        Log.d(TAG,"dealSolarValue->${adType.toString()}")

        val value = adValue.valueMicros.toDouble() / 1000.0
        val adFormatType = when (adType) {
            Type.INTer -> 3
            Type.NAV -> 6
            Type.OPEN -> 2
        }

        if (SolarEngineManager.getInstance().initialized.get()){
            SolarEngineManager.getInstance().trackAdImpression(SEAdImpEventModel().apply {
                //变现平台名称
                setAdNetworkPlatform(responseInfo?.loadedAdapterResponseInfo?.adSourceName ?: "")
                //聚合平台标识,admob SDK 设置成 "admob"
                setMediationPlatform("admob")
                //展示广告的类型，实际接入的广告类型,以此例激励视频为例adType = 1
                setAdType(adFormatType)
                //变现平台的应用ID
//            setAdNetworkAppID(adSourceId)
                //变现平台的变现广告位ID
                setAdNetworkADID(id)
                //广告ECPM
                setEcpm(value)
                //变现平台货币类型
                setCurrencyType(adValue.currencyCode)
                //填充成功填TRUE即可
                setRenderSuccess(true)
            })
        }else{
            val entity = AdPaidData(
                source = responseInfo?.loadedAdapterResponseInfo?.adSourceName ?: "",
                platform = "admob",
                adType = adFormatType,
                adFormat = "",
                adUnitId = id,
                value = value,
                currency = adValue.currencyCode ?: "USD",
                isPrecache = true,
                customData = JSONObject().toString()
            )
            event("tb_rey_v_record")
            MusicData.data.adPaidData().insertEvent(entity)
        }
    }


    fun uploadSaveClickAdValueToFb() {
        val value = AppConfig.clickAdValue
        val unit = AppConfig.clickAdValueUnit
        if (value <= 0 || unit.isNullOrEmpty() || !FacebookSdk.isInitialized()) return
        val logger = AppEventsLogger.newLogger(TB.instance)
        logger.logEvent(AppEventsConstants.EVENT_NAME_AD_CLICK, value, Bundle().apply {
            putString(AppEventsConstants.EVENT_PARAM_CURRENCY, unit)
        })
        AppConfig.clickAdValue = 0.0
        AppConfig.clickAdValueUnit = null
    }

    fun uploadSaveAdValueToFb() {
        val value = AppConfig.showAdValue
        val unit = AppConfig.showAdValueUnit
        if (value <= 0 || unit.isNullOrEmpty() || !FacebookSdk.isInitialized()) return
        val realValue = value * MuteRemoteConfig.ins.updateFbAdValue
        val logger = AppEventsLogger.newLogger(TB.instance)
        logger.logPurchase(
            BigDecimal.valueOf(realValue),
            Currency.getInstance(unit)
        )
        logger.logEvent(
            AppEventsConstants.EVENT_NAME_AD_IMPRESSION,
            realValue,
            Bundle().apply {
                putString(AppEventsConstants.EVENT_PARAM_CURRENCY, unit)
            })
        AppConfig.showAdValue = 0.0
        AppConfig.showAdValueUnit = null
    }


}