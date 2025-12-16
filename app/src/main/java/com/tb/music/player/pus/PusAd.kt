package com.tb.music.player.pus

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.tb.music.player.config.MuteEvent
import com.tb.music.player.pus.PusManager.Companion.TAG_A
import com.tb.music.player.pus.view.FullScreenNativeAdActivity
import com.tb.music.player.pus.view.PusView

class PusAd () {

    private var expirationTime: Long = 0
    private var adValue: AdValue? = null
    private var isAvailable = true

    private var nativeAd: NativeAd? = null
    private var interstitialAd: InterstitialAd? = null
    private var appOpenAd: AppOpenAd? = null

    var type: Type = Type.NAV
    var key: String = ""
    private var unitId: String = ""

    // 回调
    private var closeListener: (() -> Unit)? = null
    var clickListener: (() -> Unit)? = null
    var showListener: (() -> Unit)? = null
    var onNativeLoadFailed: ((LoadAdError) -> Unit)? = null



    constructor(inter: InterstitialAd) : this() {
        this.type = Type.INTer
        this.interstitialAd = inter
        setExpiration(60)
    }

    constructor(open: AppOpenAd) : this() {
        this.type = Type.OPEN
        this.appOpenAd = open
        setExpiration(60)
    }

    constructor(native: NativeAd) : this() {
        this.type = Type.NAV
        this.nativeAd = native
        setExpiration(60)
    }

    fun setNative(ad: NativeAd, unitId: String) {
        this.nativeAd = ad
        this.unitId = unitId
        this.type = Type.NAV
        setExpiration(60)
    }

    private fun setExpiration(minutes: Int) {
        expirationTime = System.currentTimeMillis() + minutes * 60 * 1000
    }

    fun getNativeListener(listener: (LoadAdError) -> Unit): AdListener {
        this.onNativeLoadFailed = listener
        return nativeListener
    }

    private val nativeListener = object : AdListener() {
        override fun onAdClicked() {
            Log.d(TAG_A, "[$key] NativeAd clicked")
            clickListener?.invoke()
            MuteEvent.dealValueClickFb(adValue)
        }

        override fun onAdImpression() {
            Log.d(TAG_A, "[$key] NativeAd impression")
            showListener?.invoke()
            nativeAd = null
        }

        override fun onAdFailedToLoad(error: LoadAdError) {
            onNativeLoadFailed?.invoke(error)
        }
    }

    private val fullScreenCallback = object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            handleAdClosed()
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            handleAdClosed()
        }

        override fun onAdShowedFullScreenContent() {
            Log.d(TAG_A, "[$key] FullScreenAd shown")
            interstitialAd = null
            appOpenAd = null
            nativeAd = null
            showListener?.invoke()
        }

        override fun onAdClicked() {
            Log.d(TAG_A, "[$key] FullScreenAd clicked")
            clickListener?.invoke()
            MuteEvent.dealValueClickFb(adValue)
        }
    }

    private fun handleAdClosed() {
        closeListener?.invoke()
        onDestroy()
    }

    fun onClose(listener: () -> Unit) = apply { this.closeListener = listener }
    fun onClick(listener: () -> Unit) = apply { this.clickListener = listener }
    fun onShow(listener: () -> Unit) = apply { this.showListener = listener }



    fun show(key: String, activity: Activity) {
        this.key = key
        try {
            when (type) {
                Type.OPEN -> appOpenAd?.apply {
                    setOnPaidEventListener { handlePaidEvent(it, this) }
                    fullScreenContentCallback = fullScreenCallback
                    show(activity)
                } ?: closeListener?.invoke()

                Type.INTer -> interstitialAd?.apply {
                    setOnPaidEventListener { handlePaidEvent(it, this) }
                    fullScreenContentCallback = fullScreenCallback
                    show(activity)
                } ?: closeListener?.invoke()

                Type.NAV -> nativeAd?.apply {
                    setOnPaidEventListener { handlePaidEvent(it, this) }
                    FullScreenNativeAdActivity.skipToFullScreenNativeAdActivity(activity,this,fullScreenCallback)
                } ?: closeListener?.invoke()

                else -> closeListener?.invoke()
            }
        } catch (_: Exception) {
            closeListener?.invoke()
        }
    }



    fun showNative(key: String,view: PusView){
        this.key = key
        nativeAd?.let { ad ->
            ad.setOnPaidEventListener { value ->
                this.adValue = value
                MuteEvent.logEvent(key, type, value, ad.responseInfo, unitId)
            }
            view.showAd(ad)
        }
        isAvailable = false
    }



    private fun handlePaidEvent(value: AdValue,ad: Any) {
        this.adValue = value
        val responseInfo = when (ad) {
            is InterstitialAd -> ad.responseInfo
            is AppOpenAd -> ad.responseInfo
            else -> null
        }
        val unitId = when (ad) {
            is InterstitialAd -> ad.adUnitId
            is AppOpenAd -> ad.adUnitId
            else -> ""
        }
        MuteEvent.logEvent(key, type, value, responseInfo, unitId)
    }




    fun isAva(): Boolean {
        if (!isAvailable) return false
        if (System.currentTimeMillis() > expirationTime) return false

        return when (type) {
            Type.NAV -> nativeAd != null
            Type.INTer -> interstitialAd != null
            Type.OPEN -> appOpenAd != null
        }
    }

    fun onDestroy() {
        isAvailable = false
        nativeAd?.destroy()
        nativeAd = null
        interstitialAd = null
        appOpenAd = null
    }


}