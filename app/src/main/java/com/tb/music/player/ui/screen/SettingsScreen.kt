package com.tb.music.player.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.tb.music.player.R
import com.tb.music.player.TB
import com.tb.music.player.ui.screen.internet.InternetSettings
import com.tb.music.player.ui.screen.nointernet.NoInternetSettings

@Composable
fun SettingsScreen() {
    Box(modifier = Modifier.windowInsetsPadding(WindowInsets(0, 0, 0, 0))) {
        // 背景图片
        Image(
            painter = painterResource(id = R.drawable.img_home_bg), // 替换为你的图片资源
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // 填充整个Box
        )
//        if (TB.instance.isBuy) {
//            InternetSettings()
//        } else {
//            NoInternetSettings()
//        }
        NoInternetSettings()
    }
}
