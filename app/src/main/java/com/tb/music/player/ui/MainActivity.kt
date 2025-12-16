package com.tb.music.player.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.tb.music.player.ui.router.Router
import com.tb.music.player.ui.screen.AppNav
import com.tb.music.player.ui.screen.StartScreenController

class MainActivity : ComponentActivity() {


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.Transparent.toArgb()
        setContent {
            val navController = rememberNavController()

            // ✅ 只 bind 一次
            LaunchedEffect(Unit) {
                Router.bind(navController)
            }

            AppNav(navController)

            // ✅ 前后台 StartScreen 控制
            StartScreenController(navController)
        }
    }

    override fun onResume() {
        super.onResume()
    }

}



