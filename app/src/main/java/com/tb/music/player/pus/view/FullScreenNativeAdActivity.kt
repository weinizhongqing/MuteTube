package com.tb.music.player.pus.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.tb.music.player.config.MuteRemoteConfig
import com.tb.music.player.databinding.LayoutNativefBinding
import com.tb.music.player.utils.dip2px

class FullScreenNativeAdActivity : AppCompatActivity() {

    private val binding by lazy { LayoutNativefBinding.inflate(layoutInflater) }
    private var countDownTimer: CountDownTimer? = null
    companion object{
        private var nativeAd: NativeAd? = null
        private var fullScreenContentCallback: FullScreenContentCallback? = null
        fun skipToFullScreenNativeAdActivity(context: Context,nativeAd: NativeAd,fullScreenContentCallback: FullScreenContentCallback){
            this.nativeAd = nativeAd
            this.fullScreenContentCallback = fullScreenContentCallback
            context.startActivity(Intent(context, FullScreenNativeAdActivity    ::class.java))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        statusBar(binding.root)
        val nativeAd = nativeAd
        if (nativeAd == null) {
            fullScreenContentCallback?.onAdFailedToShowFullScreenContent(
                AdError(100, "NativeAd is null", "MediaFullScreenNativeAdActivity")
            )
            finish()
            return
        }

        binding.apply {

            fullScreenNativeAd.mediaView = mediaView
            fullScreenNativeAd.headlineView = titleText
            fullScreenNativeAd.bodyView = descText
            fullScreenNativeAd.callToActionView = installButton
            val imageView: ImageView = iconImage
            imageView.clipToOutline = true
            fullScreenNativeAd.iconView = imageView
            titleText.text = nativeAd.headline
            mediaView.mediaContent = nativeAd.mediaContent
            descText.isVisible = false
            installButton.isVisible = false
            iconImage.isVisible = false


            nativeAd.icon?.let {
                iconImage.isVisible = true
                iconImage.setImageDrawable(it.drawable)
            }

            nativeAd.mediaContent?.let {
                mediaView.mediaContent = it
            }

            nativeAd.headline?.let {
                titleText.text = it
            }

            nativeAd.body?.let {
                descText.isVisible = true
                descText.text = it
            }

            nativeAd.callToAction?.let {
                installButton.isVisible = true
                installButton.text = it
            }

            fullScreenNativeAd.setNativeAd(nativeAd)
            fullScreenContentCallback?.onAdShowedFullScreenContent()
        }

        if (MuteRemoteConfig.ins.isShowCountdown) {
            binding.showCountdown.isVisible = true
            binding.imageClose.isVisible = false
            binding.showCountdown.text = "${MuteRemoteConfig.ins.fullScreenNativeCountdownTime}s"
            countDownTimer?.cancel()
            countDownTimer = object : CountDownTimer((MuteRemoteConfig.ins.fullScreenNativeCountdownTime * 1000), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    binding.showCountdown.text = "${millisUntilFinished / 1000}s"
                }

                override fun onFinish() {
                    binding.imageClose.isVisible = true
                    binding.showCountdown.isVisible = false
                }
            }
            countDownTimer?.start()
        } else {
            binding.imageClose.isVisible = true
            binding.showCountdown.isVisible = false
        }
        binding.imageClose.setOnClickListener {
            finish()
            fullScreenContentCallback?.onAdDismissedFullScreenContent()
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        countDownTimer = null
        fullScreenContentCallback = null
    }


    private fun statusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                v.updatePadding(top = statusBarHeight)
                insets
            }
        } else {
            view.doOnLayout {
                val statusBarHeight = getStatusBarHeight(this)
                view.updatePadding(top = statusBarHeight)
            }
        }
    }

    @SuppressLint("InternalInsetResource")
    private fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier(
            "status_bar_height", "dimen", "android"
        )
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            this.dip2px(24)
        }
    }

}