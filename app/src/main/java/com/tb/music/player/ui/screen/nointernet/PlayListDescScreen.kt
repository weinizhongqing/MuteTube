package com.tb.music.player.ui.screen.nointernet

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.tb.music.player.ui.theme.Color6A6F87
import com.tb.music.player.utils.LocalizedContext


@Composable
fun PlayListDescScreen(info: PlayListInfo?) {

    val musicList = remember { mutableStateListOf<MusicInfo>() }

    // 加载数据
    LaunchedEffect(Unit) {
        info?.let {
            MusicDataHelper.getPlayListMusic(it.playerListId) { it1 ->
                musicList.addAll(it1)
            }
        }
    }

    val cover = when(info?.playerListType) {
        PlayListInfo.PlayListType.TYPE_COLLECT -> R.drawable.img_my_music_like_tip
        PlayListInfo.PlayListType.TYPE_CUSTOMIZE -> R.drawable.img_my_music_custom_tip
        PlayListInfo.PlayListType.TYPE_RECENTLY -> R.drawable.img_my_music_recently_tip
        else -> 0
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
    LocalizedContext { context1 ->
        Box(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets(0, 0, 0, 0))
                .background(color = Color.Black)
                .navigationBarsPadding()
        ) {
            // 背景图片
            Image(
                painter = painterResource(id = R.drawable.img_home_bg), // 替换为你的图片资源
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

                    Image(
                        painter = painterResource(id = cover), // 替换为你的图片资源
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Crop // 填充整个Box
                    )

                    Spacer(Modifier.height(16.dp))


                    Text(
                        text = info?.playerListName ?: "",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )

                    Text(
                        text = context1.getString(R.string.num_songs, info?.musicCount ?: 0.toString()),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color6A6F87
                    )

                    Spacer(Modifier.height(16.dp))

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
}
