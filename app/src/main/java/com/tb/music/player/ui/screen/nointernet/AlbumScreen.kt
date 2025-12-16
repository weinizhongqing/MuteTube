package com.tb.music.player.ui.screen.nointernet

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.tb.music.player.R
import com.tb.music.player.music.db.MusicDataHelper
import com.tb.music.player.music.db.info.PlayListInfo
import com.tb.music.player.pus.AdLoadingDialog
import com.tb.music.player.pus.BackAdHandler
import com.tb.music.player.pus.PusPos
import com.tb.music.player.pus.rememberAdController
import com.tb.music.player.ui.router.Router
import com.tb.music.player.ui.theme.Color6A6F87
import com.tb.music.player.utils.LocalizedContext


@Composable
fun AlbumScreen() {

    val playfairDisplaySemibold = FontFamily(
        Font(R.font.playfair_display_black_italic, weight = FontWeight.Bold)
    )

    val playListInfoList = remember { mutableStateListOf<PlayListInfo>() }

    // 加载数据
    LaunchedEffect(Unit) {
        MusicDataHelper.getPlayList(PlayListInfo.PlayListType.TYPE_ALBUM) {
            playListInfoList.addAll(it)
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

    LocalizedContext { context1 ->

        Box(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets(0, 0, 0, 0))
                .navigationBarsPadding()
        ) {
            // 背景图片
            Image(
                painter = painterResource(id = R.drawable.img_single_bg), // 替换为你的图片资源
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
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

                Spacer(Modifier.height(36.dp))

                Text(
                    text = context1.getString(R.string.album),
                    fontSize = 48.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    fontFamily = playfairDisplaySemibold
                )

                Spacer(Modifier.height(56.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2), // 每行 3 个
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(playListInfoList.size) { index ->
                            AlbumItem(playListInfo = playListInfoList[index])
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AlbumItem(playListInfo: PlayListInfo) {
    Column(
        modifier = Modifier.fillMaxWidth().height(160.dp)
            .clickable {
                Log.d("AlbumItem", "click: $playListInfo")
                Router.Route.navigateNoAlbumList(playListInfo)
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_album_item_tip),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = playListInfo.playerListName,
            fontSize = 16.sp,
            color = Color.White,
            maxLines = 1,
            fontWeight = FontWeight.Normal
        )
    }
}
