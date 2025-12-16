package com.tb.music.player.pus.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.tb.music.player.TB
import com.tb.music.player.databinding.LayoutNativeBinding
import com.tb.music.player.databinding.LayoutNativevBinding

class PusView : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet?) : super(context, attr)
    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(context, attr, defStyleAttr)


    private var nativeAd: NativeAd? = null

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        nativeAd?.destroy()
        nativeAd = null
    }

    fun showAd(nativeAd: NativeAd) {
        this.nativeAd?.destroy()
        this.nativeAd = nativeAd
        val adView = getNativeAdView()
        (adView.headlineView as TextView).apply {
            this.text = nativeAd.headline
        }
        adView.bodyView?.isVisible = false
        adView.callToActionView?.isVisible = false
        adView.iconView?.isVisible = false
        nativeAd.body?.let {
            adView.bodyView?.isVisible = true
            (adView.bodyView as TextView).apply {
                this.text = it
            }
        }
        nativeAd.callToAction?.let {
            adView.callToActionView?.isVisible = true
            (adView.callToActionView as TextView).text = it
        }
        nativeAd.icon?.let {
            adView.iconView?.isVisible = true
            (adView.iconView as ImageView).setImageDrawable(it.drawable)
        }
        nativeAd.mediaContent?.let {
            adView.mediaView?.mediaContent = it
        }
        adView.setNativeAd(nativeAd)
        removeAllViews()
        addView(adView)

    }


    private fun createAdViewNormal(): NativeAdView {
        val binding = LayoutNativeBinding.inflate(LayoutInflater.from(context))
        val adView = binding.root
        adView.headlineView = binding.titleText
        adView.bodyView = binding.descText
        adView.mediaView = binding.mediaView
        adView.callToActionView = binding.installButton
        adView.iconView = binding.iconImage
        return adView
    }

    private fun createAdViewVip(): NativeAdView {
        val binding = LayoutNativevBinding .inflate(LayoutInflater.from(context))
        val adView = binding.root
        adView.headlineView = binding.titleText
        adView.bodyView = binding.descText
        adView.mediaView = binding.mediaView
        adView.callToActionView = binding.installButton
        adView.iconView = binding.iconImage
        return adView
    }


    private fun getNativeAdView(): NativeAdView {
        return when (TB.instance.isBuy) {
            true -> createAdViewVip()
            else -> createAdViewNormal()
        }
    }


}