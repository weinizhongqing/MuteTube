package com.tb.music.player.ui.screen.nointernet

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tb.music.player.R
import com.tb.music.player.music.MusicPlayerHelper
import com.tb.music.player.music.db.MusicDataHelper
import com.tb.music.player.music.db.info.MusicInfo
import com.tb.music.player.music.db.info.PlayListInfo
import com.tb.music.player.pus.AdLoadingDialog
import com.tb.music.player.pus.BackAdHandler
import com.tb.music.player.pus.PusPos
import com.tb.music.player.pus.rememberAdController
import com.tb.music.player.ui.router.Router
import com.tb.music.player.ui.screen.internet.gradientBorder

@Composable
fun NoSingerListScreen(info: PlayListInfo?) {
    Log.d("NoSingerListScreen", "info: $info")

    val musicList = remember { mutableStateListOf<MusicInfo>() }

    // 加载数据
    LaunchedEffect(Unit) {
        info?.let {
            MusicDataHelper.getPlayListMusic(it.playerListId) { it1 ->
                musicList.addAll(it1)
            }
        }
    }

    BackAdHandler(
        position = PusPos.AD_BACK_I,
        isVipCheck = true
    ) {
        Router.back()
    }

    var showLoading by remember { mutableStateOf(false) }
    val adController = rememberAdController()
    AdLoadingDialog(show = showLoading)

    fun handleBack() {
        adController.showDelayInterAd(
            position = PusPos.AD_BACK_I,
            onLoading = {showLoading = false}
        ) {
            Router.back()
        }
    }

    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets(0, 0, 0, 0))
            .background(color = Color.Black)
            .navigationBarsPadding()
    ) {
        // 背景图片
        Image(
            painter = painterResource(id = R.drawable.img_music_info_bg), // 替换为你的图片资源
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            contentScale = ContentScale.Crop // 填充整个Box
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {

            Image(
                painter = painterResource(id = R.drawable.img_back_logo),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .size(24.dp)
                    .clickable {
                        // 处理点击事件
                        handleBack()
                    },
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Spacer(modifier = Modifier.height(200.dp))

                Text(
                    text = info?.playerListName ?: "",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

            }

            Spacer(modifier = Modifier.height(100.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .gradientBorder(
                        colors = listOf(
                            Color(0x2BFFFFFF),
                            Color(0x00FFFFFF)
                        ),
                        cornerRadius = 42.dp,
                        strokeWidth = 1.dp
                    )
            ) {
                PlayAllButton(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF7165FF),
                                    Color(0xFF0059FF)
                                )
                            ),
                            shape = RoundedCornerShape(88.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    MusicPlayerHelper.clearPlayList()
                    MusicPlayerHelper.addMusicToPlayList(musicList)
                    if (musicList.isNotEmpty()) {
                        MusicPlayerHelper.playMusic(musicList.first(), true)
                    }
                    Router.navigate(Router.Route.playMusic(true))
                }



                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    items(musicList.size) { index ->
                        SinglePlayList(index, musicList[index], musicList)
                    }
                }
            }

        }
    }
}