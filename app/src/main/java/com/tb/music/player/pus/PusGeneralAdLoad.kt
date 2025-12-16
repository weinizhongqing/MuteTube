package com.tb.music.player.pus

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.tb.music.player.R
import com.tb.music.player.TB
import com.tb.music.player.config.MuteEvent
import com.tb.music.player.pus.PusPos.isEnable
import com.tb.music.player.pus.view.PusView
import com.tb.music.player.utils.LocalizedContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun BackAdHandler(
    position: String,
    isVipCheck: Boolean = true,
    onBack: () -> Unit
) {
    val adController = rememberAdController()

    // 等价 onCreate -> 预加载
    LaunchedEffect(Unit) {
        adController.preLoadAd(position, isVipCheck)
    }

    var showLoading by remember { mutableStateOf(false) }

    AdLoadingDialog(show = showLoading)


    // 等价 onBackPressedDispatcher
    BackHandler(enabled = true) {
        adController.showDelayInterAd(
            position = position,
            onLoading = {showLoading = false}
        ) {
            onBack()
        }
    }
}




@Composable
fun rememberPusView(): PusView {
    val context = LocalContext.current
    return remember {
        PusView(context)
    }
}



@Composable
fun OnResumeEffect(
    onResume: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onResume()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}



@Composable
fun rememberAdController(): ComposeAdController {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val scope = rememberCoroutineScope()
    return remember {
        ComposeAdController(activity, scope)
    }
}


@Composable
fun AdLoadingDialog(show: Boolean) {
    if (!show) return

    // 1. 加载动画资源
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ad_loading))

    // 2. 控制动画播放状态
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever, // 无限循环
        isPlaying = true,
        speed = 1.0f
    )

    LocalizedContext { context ->

        Dialog(onDismissRequest = {}) {

            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth()
                    .background(Color.White, RoundedCornerShape(16.dp)).padding(horizontal = 30.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))


                Text(
                    text = context.getString(R.string.ad_loading),
                    color = Color(0xFF0B0C0D),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

    }
}



@Stable
class ComposeAdController(
    private val activity: ComponentActivity,
    private val scope: CoroutineScope
) {

    private var delayJob: Job? = null
    private var delayFlag by mutableStateOf(false)

    /** 预加载 */
    fun preLoadAd(position: String, isV: Boolean = false) {
        if (position.isEnable() && (if (isV) TB.instance.isBuy else true)) {
            scope.launch {
                PusManager.instance.preLoad(position)
            }
        }
    }

    /** 普通插屏 */
    fun showInterAd(
        position: String,
        isV: Boolean = false,
        onFinish: () -> Unit
    ) {
        if (!position.isEnable() || (isV && !TB.instance.isBuy)) {
            onFinish()
            return
        }

        MuteEvent.event("reach_$position")

        val ad = PusManager.instance.get(position)
        if (ad == null) {
            onFinish()
            return
        }

        MuteEvent.event("padding_$position")

        ad.onClick {
            MuteEvent.event("clk_$position")
        }.onShow {
            MuteEvent.event("reveal_$position")
            scope.launch {
                PusManager.instance.preLoad(position)
            }
        }.onClose {
            onFinish()
        }.show(position, activity)
    }

    /** 延迟插屏（带 loading） */
    fun showDelayInterAd(
        position: String,
        onLoading: (Boolean) -> Unit,
        onFinish: () -> Unit
    ) {
        if (!position.isEnable()) {
            onFinish()
            return
        }

        val ad = PusManager.instance.get(position)
        if (ad != null) {
            showAd(position, ad, onFinish)
            return
        }

        delayFlag = true
        onLoading(true)

        // 超时兜底
        delayJob = scope.launch {
            delay(5_000)
            if (!delayFlag) return@launch

            delayFlag = false
            onLoading(false)
            showAd(position, onFinish)
        }

        // 主加载
        scope.launch {
            val loadedAd = PusManager.instance.load(position)
            if (!delayFlag) return@launch

            delayFlag = false
            delayJob?.cancel()
            onLoading(false)
            showAd(position, loadedAd, onFinish)
        }
    }

    private fun showAd(
        position: String,
        onFinish: () -> Unit
    ) {
        val ad = PusManager.instance.get(position)
        if (ad == null) {
            onFinish()
            return
        }
        showAd(position, ad, onFinish)
    }

    private fun showAd(
        position: String,
        ad: PusAd?,
        onFinish: () -> Unit
    ) {
        if (ad == null) {
            onFinish()
            return
        }

        ad.onClick {
            MuteEvent.event("clk_$position")
        }.onShow {
            MuteEvent.event("reveal_$position")
            scope.launch {
                PusManager.instance.preLoad(position)
            }
        }.onClose {
            onFinish()
        }.show(position, activity)
    }

    /** 原生广告 */
    fun showNativeAd(
        position: String,
        view: PusView,
        isV: Boolean = false
    ) {
        scope.launch {
            val start = System.currentTimeMillis()
            PusManager.instance.load(position)
            val cost = System.currentTimeMillis() - start
            if (cost < 300) delay(300 - cost)

            if (position.isEnable() && (if (isV) TB.instance.isBuy else true)) {
                MuteEvent.event("reach_$position")
                val ad = PusManager.instance.get(position)
                ad?.onShow {
                    MuteEvent.event("reveal_$position")
                }?.onClick {
                    MuteEvent.event("clk_$position")
                }?.showNative(position, view)
            }
        }
    }
}


