package com.tb.music.player.pus

import android.content.Context
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.code
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object LoaderCode {

    suspend fun loadByType(
        context: Context,
        adUnit: NodeUnit,
        type: Type
    ): PusAd? {
        return when (adUnit.type) {
            Type.NAV -> loadAd(context, adUnit.id, type, Type.NAV)
            Type.INTer -> loadAd(context, adUnit.id, type, Type.INTer)
            Type.OPEN -> loadAd(context, adUnit.id, type, Type.OPEN)
        }
    }

    private suspend fun loadAd(
        context: Context,
        adId: String,
        type: Type,
        zType: Type
    ):  PusAd = suspendCancellableCoroutine { cont ->
        when (zType) {
            Type.NAV -> {
                val wrapper = PusAd()
                val loader = AdLoader.Builder(context, adId)
                    .forNativeAd { native ->
                        wrapper.setNative(native, adId)
                        PusCache.addCache(type, wrapper)
                        cont.resume(wrapper)
                    }
                    .withAdListener(wrapper.getNativeListener {
                        cont.resumeWithException(Exception(it.code.toString()))
                    })
                    .withNativeAdOptions(NativeAdOptions.Builder().build())
                    .build()
                loader.loadAd(AdRequest.Builder().build())
            }

            Type.INTer -> {
                InterstitialAd.load(
                    context, adId, AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(ad: InterstitialAd) {
                            val wrapper = PusAd(ad)
                            PusCache.addCache(type, wrapper)
                            cont.resume(wrapper)
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            cont.resumeWithException(Exception(error.code.toString()))
                        }
                    })
            }

            Type.OPEN -> {
                AppOpenAd.load(
                    context, adId, AdRequest.Builder().build(),
                    object : AppOpenAd.AppOpenAdLoadCallback() {
                        override fun onAdLoaded(ad: AppOpenAd) {
                            val wrapper = PusAd(ad)
                            PusCache.addCache(type, wrapper)
                            cont.resume(wrapper)
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            cont.resumeWithException(Exception(error.code.toString()))
                        }
                    })
            }
        }
    }


}