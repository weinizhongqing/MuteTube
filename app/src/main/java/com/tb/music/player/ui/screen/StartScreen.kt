package com.tb.music.player.ui.screen

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.protobuf.type
import com.tb.music.player.AppForegroundState
import com.tb.music.player.R
import com.tb.music.player.TB
import com.tb.music.player.config.AppConfig
import com.tb.music.player.config.MuteEvent
import com.tb.music.player.pus.PusAd
import com.tb.music.player.pus.PusManager
import com.tb.music.player.pus.PusPos
import com.tb.music.player.pus.PusPos.isEnable
import com.tb.music.player.pus.rememberAdController
import com.tb.music.player.ui.router.Router
import com.tb.music.player.ui.theme.Color29FFFFFF
import com.tb.music.player.utils.LocalizedContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

@Composable
fun StartScreen(type : Boolean) {

    Log.d("StartScreen", "type: $type")

    val context = LocalContext.current
    val activity = context as ComponentActivity

    val adController = rememberAdController()


    RequestNotificationPermission{
        requestGoogleUmp(activity){

            activity.lifecycleScope.launch {

                delay(500)

                if (PusPos.AD_START.isEnable()) {
                    MuteEvent.event("reach_${PusPos.AD_START}")
                }

                if (!AppConfig.appFirstSelectLanguage){
                    listOf(
                        PusPos.AD_LANGUAGE_I,
                        PusPos.AD_LANGUAGE_N,
                    ).forEach { adController.preLoadAd(it, true) }

                }else{
                    listOf(
                        PusPos.AD_CLICK_I
                    ).forEach { adController.preLoadAd(it, true) }
                }

                launch {
                    val ad = PusManager.instance.get(PusPos.AD_START)
                    if (ad != null) {
                        displayAd(activity,ad,type)
                    } else {
                        val loadStart = System.currentTimeMillis()
                        withTimeoutOrNull(16000) {
                            PusManager.instance.preLoad(PusPos.AD_START)
                        }
                        val loadTime = (System.currentTimeMillis() - loadStart) / 1000
                        MuteEvent.event("start_load_t", "msg", loadTime.toString())
                        val fallbackAd = PusManager.instance.get(PusPos.AD_START)
                        displayAd(activity,fallbackAd,type)
                    }
                }
            }

        }
    }


    val rokkitt = FontFamily(
        Font(R.font.rokkitt_thin, weight = FontWeight.Thin)
    )

    val playfairDisplaySemibold = FontFamily(
        Font(R.font.playfair_display_semibold, weight = FontWeight.SemiBold)
    )

    // 1. 加载动画资源
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.start_loading))

    // 2. 控制动画播放状态
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever, // 无限循环
        isPlaying = true,
        speed = 1.0f
    )

    LocalizedContext { context ->

        Box(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets(0, 0, 0, 0))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_start_bg), // 替换为你的图片资源
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // 填充整个Box
            )

            Text(
                text = context.getString(R.string.music),
                color = Color29FFFFFF,
                fontFamily = rokkitt,
                modifier = Modifier.padding(top = 48.dp, start = 14.dp, end = 14.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 74.sp,
                    letterSpacing = 20.sp
                )
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_start_content),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 70.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.img_start_app_logo),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 41.dp).size(74.dp)
                )


                Text(
                    text =context.getString( R.string.music_player),
                    color = Color.White,
                    fontFamily = playfairDisplaySemibold,
                    modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold
                )


                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.padding(top = 40.dp, bottom = 100.dp).width(40.dp)
                        .height(10.dp)
                )
            }
        }
    }
}

private fun displayAd(activity: ComponentActivity, ad: PusAd?,runType: Boolean) {
    if (!AppForegroundState.isForeground) {
        activity.finish()
        return
    }
    val adPosition = PusPos.AD_START

    ad?.let {
        MuteEvent.event("padding_$adPosition")
        it.onClose { navigateNext(runType) }
        it.onClick { MuteEvent.event("clk_$adPosition") }
        it.onShow {
            MuteEvent.event("reveal_$adPosition")
            activity.lifecycleScope.launch {
                PusManager.instance.preLoad(PusPos.AD_START)
            }
        }
        it.show(adPosition, activity)
    } ?: run {
        navigateNext(runType)
    }
}


private fun navigateNext(runType: Boolean){
    if (!AppConfig.appFirstSelectLanguage) {
        Router.navigate(Router.Route.LANGUAGE, popUpToRoute = Router.Route.START, inclusive = true)
    } else if (!AppConfig.isScanLocalMusicFirst){
        Router.navigate(Router.Route.SCAN_LOCAL_MUSIC, popUpToRoute = Router.Route.START, inclusive = true)
    }else {
        if (runType){
            Router.back()
        }else{
            Router.navigate(Router.Route.MAIN, popUpToRoute = Router.Route.START, inclusive = true)
        }
    }



}

@Composable
fun RequestNotificationPermission(
    onResult: (granted: Boolean) -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        onResult(granted)
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onResult(true)
            return@LaunchedEffect
        }

        val granted = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            onResult(true)
        } else {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}


fun requestGoogleUmp(
    activity: ComponentActivity,
    onFinished: () -> Unit
) {
    if (AppConfig.isRepUmp) {
        onFinished()
        return
    }

    val consentInfo = UserMessagingPlatform.getConsentInformation(activity)
    val params = ConsentRequestParameters.Builder().build()

    consentInfo.requestConsentInfoUpdate(
        activity,
        params,
        {
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                AppConfig.isRepUmp = true

                if (consentInfo.canRequestAds()) {
                    runCatching {
                        MobileAds.initialize(TB.instance)
                    }
                }

                onFinished()
            }
        },
        {
            // 获取 consentInfo 失败，也继续流程
            AppConfig.isRepUmp = true
            onFinished()
        }
    )
}







@Composable
fun StartScreenController(
    navController: NavController
) {
    val isForeground = AppForegroundState.isForeground
    val hasEnteredBackground = AppForegroundState.hasEnteredBackground

    val backStackEntry by navController.currentBackStackEntryAsState()

    // ❗ NavHost 尚未 ready，直接返回
    if (backStackEntry == null) return

    val currentRoute = backStackEntry?.destination?.route

    var handledThisForeground by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isForeground) {

        if (isForeground) {

            // ❌ 冷启动，跳过
            if (!hasEnteredBackground) return@LaunchedEffect

            // ❌ 当前已经是 Start
            if (currentRoute == Router.Route.START) return@LaunchedEffect

            // ❌ 本次前台已处理
            if (handledThisForeground) return@LaunchedEffect

            handledThisForeground = true

            if (
                TB.instance.isBuy &&
                !TB.ignoreStart &&
                currentRoute != Router.Route.START
            ) {
                Router.navigate(Router.Route.start(true))
            }

            TB.ignoreStart = false
        } else {
            handledThisForeground = false
        }
    }
}




