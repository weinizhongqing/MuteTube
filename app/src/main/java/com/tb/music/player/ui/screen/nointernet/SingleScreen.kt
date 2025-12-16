package com.tb.music.player.ui.screen.nointernet

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.tb.music.player.R
import com.tb.music.player.TB
import com.tb.music.player.music.MusicPlayerHelper
import com.tb.music.player.music.db.MusicDataHelper
import com.tb.music.player.music.db.info.MusicInfo
import com.tb.music.player.music.db.info.PlayListInfo
import com.tb.music.player.pus.AdLoadingDialog
import com.tb.music.player.pus.BackAdHandler
import com.tb.music.player.pus.OnResumeEffect
import com.tb.music.player.pus.PusPos
import com.tb.music.player.pus.rememberAdController
import com.tb.music.player.pus.rememberPusView
import com.tb.music.player.ui.router.Router
import com.tb.music.player.ui.screen.ButtonBox
import com.tb.music.player.ui.screen.internet.gradientBorder
import com.tb.music.player.ui.theme.Color393C4C
import com.tb.music.player.ui.theme.Color646C93
import com.tb.music.player.ui.theme.Color6A6F87
import com.tb.music.player.utils.LocalizedContext
import com.tb.music.player.utils.shareSong
import java.io.File

@Composable
fun SingleScreen() {

    val playfairDisplaySemibold = FontFamily(
        Font(R.font.playfair_display_black_italic, weight = FontWeight.Bold)
    )

    val musicList = remember { mutableStateListOf<MusicInfo>() }

    // 加载数据
    LaunchedEffect(Unit) {
        MusicDataHelper.getPlayListMusic(MusicDataHelper.singleListId) { list ->
            musicList.addAll(list)
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

                Column(modifier = Modifier.weight(1f)){
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
                        text = context1.getString(R.string.single),
                        fontSize = 48.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        fontFamily = playfairDisplaySemibold
                    )

                    Spacer(Modifier.height(56.dp))

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
    }
}


@Composable
fun SinglePlayList(index: Int, musicInfo: MusicInfo,mutableList: MutableList<MusicInfo>) {

    var isPlaying by remember { mutableStateOf(MusicPlayerHelper.isPlayingMusic(musicInfo)) }

    var isShowBottomSheet by remember { mutableStateOf(false) }

    var isShowAddListBottomSheet by remember { mutableStateOf(false) }

    var isCreateBottomSheet by remember { mutableStateOf(false) }

    // 1. 加载动画资源
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.play_music_loading))

    // 2. 控制动画播放状态
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever, // 无限循环
        isPlaying = true,
        speed = 1.0f
    )

    val backgroundModifier = if (isPlaying) {
        Modifier.background(
            brush = Brush.horizontalGradient(
                colors = listOf(Color(0x1A0059FF), Color(0xFF0059FF))
            ),
            shape = RoundedCornerShape(20.dp)
        )

    } else {
        Modifier.background(
            color = Color(0xFF20222B),
            shape = RoundedCornerShape(20.dp)
        )
    }

    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .then(backgroundModifier)
            .padding(16.dp).clickable{
                MusicPlayerHelper.addMusicToPlayList(musicInfo)
                MusicPlayerHelper.playMusic(musicInfo,true)
                Router.navigate(Router.Route.playMusic(true))
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(21.dp)) {
            if (isPlaying) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier
                        .height(18.dp)
                        .width(17.dp)
                        .align(Alignment.Center)
                )
            } else {
                Text(
                    text = (index + 1).toString(),
                    fontSize = 18.sp,
                    color = Color646C93,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = musicInfo.displayName,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Text(
                text = musicInfo.singerName,
                fontSize = 14.sp,
                color = Color646C93,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }

        Image(
            painter = painterResource(id = R.drawable.img_home_tip_menu),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 12.dp)
                .size(24.dp).clickable{
                    isShowBottomSheet = true
                }
        )

    }

    if (isShowBottomSheet) {
        MusicCardBottomSheet(musicInfo,true){
            isShowBottomSheet = false
            if (it){
                isShowAddListBottomSheet = true
            }
        }
    }

    if (isShowAddListBottomSheet){
        AddMusicBottomSheet(
            musicInfo = musicInfo,
            showSheet = true,
            onDismiss = {
                isShowAddListBottomSheet = false
                if (it){
                    isCreateBottomSheet = true
                }
            }
        )
    }

    if (isCreateBottomSheet){
        CreatePlayListBottomSheet(
            musicInfo = musicInfo,
            onClose = {
                isCreateBottomSheet = false
            }
        )
    }


}


@Composable
fun PlayAllButton(
    modifier: Modifier = Modifier,
    clickable: () -> Unit
) {
    LocalizedContext { context1 ->
        Row(modifier = modifier.clickable {
            clickable()
        }, horizontalArrangement = Arrangement.Center) {

            Image(
                painter = painterResource(id = R.drawable.img_all_play),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(4.dp))

            Text(
                text = context1.getString(R.string.play_all),
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Normal
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicCardBottomSheet(
    musicInfo: MusicInfo,
    showSheet: Boolean,
    onDismiss: (Boolean) -> Unit
) {
    if (showSheet) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        // 设置全宽全屏
        ModalBottomSheet(
            onDismissRequest = { onDismiss(false) },
            sheetState = sheetState,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(), // 保证底部不挡导航栏
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            scrimColor = Color(0x80000000) // 遮罩透明度可调
        ) {
            MusicCard(
                musicInfo = musicInfo,
                onClose = { onDismiss(it) }
            )
        }
    }
}


@Composable
fun MusicCard(musicInfo: MusicInfo, onClose: (Boolean) -> Unit) {
    LocalizedContext { context1 ->
        val isCollect by remember {
            mutableStateOf(MusicDataHelper.isCollect(musicInfo))
        }
        Log.d("MusicCard", "isCollect: $isCollect")

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFF1F1F28),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(), contentAlignment = Alignment.TopEnd
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_music_info_close),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onClose(false) }
                )
            }

            // 歌曲封面
            Image(
                painter = painterResource(id = R.drawable.img_music_info_default_cover), // 替换成你的图片资源
                contentDescription = null,
                modifier = Modifier
                    .size(116.dp)
                    .padding(vertical = 8.dp)
                    .offset(y = (-50).dp)
                    .align(Alignment.TopCenter)
            )


            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(46.dp))

                // 歌名
                Text(
                    text = musicInfo.displayName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )

                // 歌手
                Text(
                    text = musicInfo.singerName,
                    color = Color6A6F87,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(16.dp))
                Spacer(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color393C4C)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // 操作按钮
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                color = Color393C4C,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                onClose(true)
                            },
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.img_add_play_list),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = context1.getString( R.string.add_play_list),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Spacer(modifier = Modifier.width(5.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                color = Color393C4C,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                TB.instance.sendBroadcast(Intent(MusicPlayerHelper.Action.ACTION_NEXT_SONG).apply {
                                    setPackage(TB.instance.packageName)
                                })
                                onClose(false)
                            }, verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_add_next),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = context1.getString( R.string.play_next),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Spacer(modifier = Modifier.width(5.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                color = Color393C4C,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                if (isCollect) {
                                    MusicDataHelper.deleteMusicFromPlayList(
                                        MusicDataHelper.collectListId,
                                        musicInfo
                                    ) {}
                                } else {
                                    MusicDataHelper.addMusicToPlayList(
                                        MusicDataHelper.collectListId,
                                        musicInfo
                                    ) {}
                                }
                                onClose(false)
                            }, verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = if (isCollect) R.drawable.img_more_liked else R.drawable.img_more_like),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = context1.getString(R.string.favorite),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }


                }

                Spacer(modifier = Modifier.height(16.dp))

                // 歌曲详细信息
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_music_user),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = context1.getString(
                                R.string.artists_format,
                                musicInfo.singerName
                            ),
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))


                    Row(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_music_user_album),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = context1.getString( R.string.album_format, musicInfo.albumName),
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))


                    Row(modifier = Modifier.fillMaxWidth().clickable {
                        val context = TB.instance
                        val songUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            File(musicInfo.path)
                        )
                        shareSong(context, songUri)
                        onClose(false)
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.img_music_info_share),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = context1.getString(R.string.share),
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMusicBottomSheet(
        musicInfo: MusicInfo,
    showSheet: Boolean,
    onDismiss: (Boolean) -> Unit
) {
    if (showSheet) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        // 设置全宽全屏
        ModalBottomSheet(
            onDismissRequest = { onDismiss(false) },
            sheetState = sheetState,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(), // 保证底部不挡导航栏
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            scrimColor = Color(0x80000000) // 遮罩透明度可调
        ) {
            AddMusicBottomSheetContent(
                musicInfo = musicInfo,
                onClose = { onDismiss(it) }
            )
        }
    }
}
@Composable
fun AddMusicBottomSheetContent(
        musicInfo: MusicInfo,
     onClose: (Boolean) -> Unit) {
        LocalizedContext { context1 ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF1F1F28),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
            ) {

                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(), contentAlignment = Alignment.TopEnd
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_music_info_close),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onClose(false) }
                    )
                }

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = context1.getString(R.string.add_play_list),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Spacer(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color393C4C)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().height(64.dp).clickable {
                            onClose(true)
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_add_play_list_class),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = context1.getString(R.string.create_new_play_list),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }


                    val musicList = remember { mutableStateListOf<PlayListInfo>() }

                    // 加载数据
                    LaunchedEffect(Unit) {
                        MusicDataHelper.getAllPlayList(false) { list ->
                            musicList.addAll(list)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                    ) {
                        items(musicList.size) { index ->
                            AddClaseItem(musicInfo, musicList[index]) {
                                onClose(false)
                            }
                        }
                    }

                }
            }
        }
}


@Composable
fun AddClaseItem(
    musicInfo: MusicInfo,
    playListInfo: PlayListInfo
        ,onClose: () -> Unit
){
        LocalizedContext { context1 ->
            when (playListInfo.playerListType) {
                PlayListInfo.PlayListType.TYPE_COLLECT -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().height(64.dp).clickable {
                            MusicDataHelper.addMusicToPlayList(
                                playListInfo.playerListId, musicInfo
                            ) {
                                onClose()
                            }
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_add_collection),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = context1.getString(R.string.favorite_songs),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = context1.getString(
                                    R.string.num_songs,
                                    playListInfo.musicCount.toString()
                                ),
                                color = Color6A6F87,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }

                PlayListInfo.PlayListType.TYPE_LOCAL -> {
                }

                PlayListInfo.PlayListType.TYPE_CUSTOMIZE -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().height(64.dp).clickable {
                            MusicDataHelper.addMusicToPlayList(
                                playListInfo.playerListId, musicInfo
                            ) {
                                onClose()
                            }
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_add_common_play_list),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = playListInfo.playerListName,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = context1.getString(
                                    R.string.num_songs,
                                    playListInfo.musicCount.toString()
                                ),
                                color = Color6A6F87,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlayListBottomSheet(musicInfo: MusicInfo, onClose: () -> Unit){
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // 设置全宽全屏
    ModalBottomSheet(
        onDismissRequest = { onClose() },
        sheetState = sheetState,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(), // 保证底部不挡导航栏
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,
        scrimColor = Color(0x80000000) // 遮罩透明度可调
    ) {

        CreatePlayListBottomSheetContent(
            musicInfo = musicInfo,
            onClose = onClose
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlayListBottomSheetContent(
    musicInfo: MusicInfo,
    onClose: () -> Unit
){

    LocalizedContext { context1 ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFF1F1F28),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
        ) {

            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(), contentAlignment = Alignment.TopEnd
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_music_info_close),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onClose() }
                )
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = context1.getString( R.string.create_new_play_list),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(24.dp))
                Spacer(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color393C4C)
                )
                Spacer(modifier = Modifier.height(24.dp))

                var text by remember { mutableStateOf("") }

                TextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = {
                        Text(
                            context1.getString( R.string.enter_new_play_list_name),
                            color = Color6A6F87,
                            fontSize = 14.sp
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        // 文本相关
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = Color.Gray,

                        // 背景容器
                        focusedContainerColor = Color(0xFF393C4C),
                        unfocusedContainerColor = Color(0xFF393C4C),
                        disabledContainerColor = Color(0xFF393C4C),

                        // 光标
                        cursorColor = Color.White,

                        // 占位符
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray,

                        // 底部指示器（设为透明，因为你已经有圆角背景）
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFF393C4C),
                            shape = RoundedCornerShape(12.dp)
                        )
                )



                Spacer(modifier = Modifier.height(24.dp))


                ButtonBox(text = context1.getString(R.string.create)) {
                    PlayListInfo(PlayListInfo.PlayListType.TYPE_CUSTOMIZE, text)?.let {
                        MusicDataHelper.addPlayList(it) {
                            MusicDataHelper.addMusicToPlayList(
                                it.playerListId, musicInfo
                            ) {
                                onClose.invoke()
                            }
                        }
                    }
                }

            }
        }

    }
}










