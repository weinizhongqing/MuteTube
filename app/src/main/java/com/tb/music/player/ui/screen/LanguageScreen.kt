package com.tb.music.player.ui.screen

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.tb.music.player.R
import com.tb.music.player.TB
import com.tb.music.player.config.AppConfig
import com.tb.music.player.pus.AdLoadingDialog
import com.tb.music.player.pus.OnResumeEffect
import com.tb.music.player.pus.PusPos
import com.tb.music.player.pus.rememberAdController
import com.tb.music.player.pus.rememberPusView
import com.tb.music.player.ui.router.Router
import com.tb.music.player.utils.AppLangModel
import com.tb.music.player.utils.LanguageUtil
import com.tb.music.player.utils.LocalizedContext
import kotlin.collections.forEach


@Composable
fun LanguageScreen() {

    var backVisible by remember { mutableStateOf(AppConfig.appFirstSelectLanguage) }
    var okVisible by remember { mutableStateOf(!(!AppConfig.appFirstSelectLanguage && TB.instance.isBuy)) }

    var selectCode by remember { mutableStateOf("en") }

    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val adController = rememberAdController()
    val pusView = rememberPusView()
    var hasRequested by remember { mutableStateOf(false) }

    var showLoading by remember { mutableStateOf(false) }

    AdLoadingDialog(show = showLoading)

    OnResumeEffect {
        if (!hasRequested) {
            hasRequested = true
            adController.showNativeAd(
                position = PusPos.AD_LANGUAGE_N,
                view = pusView
            )
        }
    }

    LocalizedContext{ context1 ->

    Box(modifier = Modifier.windowInsetsPadding(WindowInsets(0, 0, 0, 0))) {
        // 背景图片
        Image(
            painter = painterResource(id = R.drawable.img_home_bg), // 替换为你的图片资源
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // 填充整个Box
        )
        Column( modifier = Modifier
            .fillMaxSize().statusBarsPadding().navigationBarsPadding()) {

            AppLocalTitle(
                title = context1.getString(R.string.language),
                isBackVisible = backVisible,
                isOkVisible = okVisible,
                {
                    Router.back()
                },
                {
                    adController.showDelayInterAd(
                        position = PusPos.AD_LANGUAGE_I,
                        onLoading = { showLoading = false}
                    ){
                        LanguageUtil.switchLanguage(selectCode)
                        if (!AppConfig.isScanLocalMusicFirst){
                            Router.navigate(Router.Route.SCAN_LOCAL_MUSIC ,popUpToRoute = Router.Route.LANGUAGE, inclusive = true)
                        }else if (!AppConfig.appFirstSelectLanguage){
                            Router.navigate(Router.Route.MAIN ,popUpToRoute = Router.Route.LANGUAGE, inclusive = true)
                        }else{
                            Router.back()
                        }
                        AppConfig.appFirstSelectLanguage = true
                        AppConfig.appLanguage = selectCode
                    }
                })

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                item {
                    LanguageSelector(
                        languages = LanguageUtil.langListData,{
                            okVisible = true
                            selectCode = it
                        }
                    )
                }
            }

            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { pusView }
            )
        }

    } }
}

@Composable
fun LanguageSelector(
    languages: List<AppLangModel>,
    onLanguageSelected: (String) -> Unit = {}
) {
    var isDefaultSelect by remember { mutableStateOf(!(!AppConfig.appFirstSelectLanguage && TB.instance.isBuy)) }

    Log.d("LanguageSelector", "isDefaultSelect: $isDefaultSelect")

    var selectedLanguage by remember {
        mutableStateOf(
            if (isDefaultSelect) AppConfig.appLanguage else null
        )
    }

    languages.forEach { language ->
        val isSelected = language.code == selectedLanguage
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(20.dp)) // 先裁剪形状
                .clickable {
                    selectedLanguage = language.code
                    onLanguageSelected(language.code)
                },
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Image(
                    painter = painterResource(id = R.drawable.img_select_language),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize() // 铺满 Box
                )
            } else {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(color = Color(0x337165FF), shape = RoundedCornerShape(20.dp))
                )
            }

            Text(
                text = language.name,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

