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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.tb.music.player.R
import com.tb.music.player.TB
import com.tb.music.player.config.AppConfig
import com.tb.music.player.music.MusicPlayerHelper
import com.tb.music.player.music.db.info.MusicInfo
import com.tb.music.player.music.status.PlayStatus
import com.tb.music.player.pus.AdLoadingDialog
import com.tb.music.player.pus.BackAdHandler
import com.tb.music.player.pus.PusPos
import com.tb.music.player.pus.rememberAdController
import com.tb.music.player.ui.router.Router
import com.tb.music.player.ui.screen.ModeButtonBox
import com.tb.music.player.ui.theme.Color0059FF
import com.tb.music.player.ui.theme.Color393C4C
import com.tb.music.player.ui.theme.Color6A6F87
import com.tb.music.player.utils.LocalizedContext
import com.tb.music.player.utils.formatDuration
import com.tb.music.player.utils.shareSong
import java.io.File

@Composable
fun PlayMusicScreen(type: Boolean) {

    var enterScreen by remember { mutableStateOf(type) }


    Log.d("PlayMusicScreen", "type: $type")
    Log.d("PlayMusicScreen", "enterScreen: $enterScreen")

    // 1. 加载动画资源
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.play_loading))

    // 2. 控制动画播放状态
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever, // 无限循环
        isPlaying = true,
        speed = 1.0f
    )

    val musicInfo by MusicPlayerHelper.playMusic.observeAsState()
    val collection by MusicPlayerHelper.collection.observeAsState(false)
    val playState by MusicPlayerHelper.playState.observeAsState()
    val currentPlayList by MusicPlayerHelper.playMusicList.observeAsState()


    var playMode by remember { mutableIntStateOf(AppConfig.playMode) }

    val iconRes = when (playMode) {
        0 -> R.drawable.img_order_play
        1 -> R.drawable.img_random_play
        2 -> R.drawable.img_cycle_play
        else -> 0
    }

    var isShowAddListBottomSheet by remember { mutableStateOf(false) }

    var isCreateBottomSheet by remember { mutableStateOf(false) }

    var isShowBottomSheet by remember { mutableStateOf(false) }

    var isShowPlayListBottomSheet by remember { mutableStateOf(false) }

    BackAdHandler(
        position = PusPos.AD_BACK_I,
        isVipCheck = true
    ) {
        if(enterScreen){
            Router.back()
        }else{
            Router.navigate(Router.Route.MAIN,popUpToRoute = Router.Route.PLAY_MUSIC,inclusive = true)
        }
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
            .navigationBarsPadding()
    ) {
        // 背景图片
        Image(
            painter = painterResource(id = R.drawable.img_home_bg), // 替换为你的图片资源
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
                       if (enterScreen){
                           handleBack()
                       }else{
                           Router.navigate(Router.Route.MAIN,popUpToRoute = Router.Route.PLAY_MUSIC,inclusive = true)
                       }
                    },
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(36.dp))

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            if (musicInfo != null) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = musicInfo?.displayName ?: "",
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = musicInfo?.singerName ?: "",
                        fontSize = 16.sp,
                        color = Color6A6F87,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Spacer(Modifier.height(64.dp))

            Row(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {

                Image(
                    modifier = Modifier.size(24.dp).clickable{
                        TB.instance.sendBroadcast(Intent(MusicPlayerHelper.Action.ACTION_COLLECTION_CLICK).apply {
                            setPackage(TB.instance.packageName)
                        })
                    },
                    painter = painterResource(id = if (collection){
                        R.drawable.img_more_liked
                    }else{
                        R.drawable.img_more_like
                    }),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.weight(1f))


                Image(
                    modifier = Modifier.size(24.dp).clickable{
                        val v = AppConfig.playMode + 1
                        val newV = v % 3
                        AppConfig.playMode = newV
                        playMode = newV
                    },
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.weight(1f))

                Image(
                    modifier = Modifier.size(24.dp).clickable{
                        musicInfo?.let {
                            val context = TB.instance
                            val songUri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                File(it.path)
                            )
                            shareSong(context, songUri)
                        }
                    },
                    painter = painterResource(id = R.drawable.img_play_share),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

            }

            Spacer(Modifier.height(40.dp))
            MusicSeekBar()
            Spacer(Modifier.height(36.dp))

            Row(modifier = Modifier.padding(horizontal = 39.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {

                Image(
                    modifier = Modifier.size(24.dp).clickable{
                        isShowPlayListBottomSheet = true
                    },
                    painter = painterResource(id = R.drawable.img_play_playlist),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.weight(1f))

                Image(
                    modifier = Modifier.size(24.dp).clickable{
                        TB.instance.sendBroadcast(Intent(MusicPlayerHelper.Action.ACTION_PREVIOUS).apply {
                            setPackage(TB.instance.packageName)
                        })
                    },
                    painter = painterResource(id = R.drawable.img_play_last_music),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.weight(1f))

                Image(
                    modifier = Modifier.size(65.dp).clickable{
                        TB.instance.sendBroadcast(Intent(MusicPlayerHelper.Action.ACTION_PLAY_PAUSE).apply {
                            setPackage(TB.instance.packageName)
                        })
                    },
                    painter = painterResource(id = if (playState == PlayStatus.PLAYING){
                        R.drawable.img_play_start
                    }else{
                        R.drawable.img_play_stop
                    }),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.weight(1f))

                Image(
                    modifier = Modifier.size(24.dp).clickable{
                        TB.instance.sendBroadcast(Intent(MusicPlayerHelper.Action.ACTION_NEXT_SONG).apply {
                            setPackage(TB.instance.packageName)
                        })
                    },
                    painter = painterResource(id = R.drawable.img_play_next_music),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.weight(1f))

                Image(
                    modifier = Modifier.size(24.dp).clickable{
                        isShowBottomSheet = true
                    },
                    painter = painterResource(id = R.drawable.img_home_tip_menu),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(76.dp))

        }
    }


        if (isShowPlayListBottomSheet) {
            currentPlayList?.let {
                CurrentPlayListBottomSheet(
                    currentList = it,
                    showSheet = true,
                    switchMode = {
                        playMode = it
                    },
                    onDismiss = { musicInfo ->
                        if (musicInfo != null){
                            it.remove(musicInfo)
                        }
                        isShowPlayListBottomSheet = false
                    }
                )
            }
        }



    if (isShowBottomSheet) {
        musicInfo?.let {
            MusicCardBottomSheet(
                musicInfo = it,
                showSheet = true,
                onDismiss = {
                    isShowBottomSheet = false
                    if (it){
                        isShowAddListBottomSheet = true
                    }
                }
            )
        }
    }



    if (isShowAddListBottomSheet){
        musicInfo?.let {
            AddMusicBottomSheet(
                musicInfo = it,
                showSheet = true,
                onDismiss = {
                    isShowAddListBottomSheet = false
                    if (it){
                        isCreateBottomSheet = true
                    }
                }
            )
        }

    }

    if (isCreateBottomSheet){
        musicInfo?.let {
            CreatePlayListBottomSheet(
                musicInfo = it,
                onClose = {
                    isCreateBottomSheet = false
                }
            )
        }

    }

}


@Composable
fun MusicSeekBar() {
    val playProgress by MusicPlayerHelper.playProgress.observeAsState()

    // duration = 总时长
    // position = 当前进度
    val duration = playProgress?.first?.toFloat() ?: 0f
    val position = playProgress?.second?.toFloat() ?: 0f

    var sliderValue by remember(position) { mutableFloatStateOf(position) }
    var isUserDragging by remember { mutableStateOf(false) }

    GradientSeekBar(
        value = sliderValue,
        onValueChange = {
            sliderValue = it
            isUserDragging = true
        },
        onValueChangeFinished = {
            isUserDragging = false
            MusicPlayerHelper.savePlayState()
            MusicPlayerHelper.seekTo(sliderValue.toLong())
            MusicPlayerHelper.restorePlayState()
        },
        valueRange = 0f..duration
    )


    // 显示时间
    Row(
        modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(formatDuration(position.toLong()), color = Color.White)
        Spacer(Modifier.weight(1f))
        Text(formatDuration(duration.toLong()), color = Color.White)
    }

    // 播放器进度更新 → UI 自动更新（用户未拖动时）
    LaunchedEffect(position) {
        if (!isUserDragging) {
            sliderValue = position
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradientSeekBar(
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        valueRange = valueRange,
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .height(30.dp),   // 让轨道更细、更接近你图里的视觉
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            disabledThumbColor = Color.White,

            // ⚡ 不使用默认轨道颜色，下面自定义轨道
            activeTrackColor = Color.Transparent,
            inactiveTrackColor = Color.Transparent,
        ),
        track = { sliderState ->
            val progressFraction = sliderState.value / sliderState.valueRange.endInclusive

            Box(
                modifier = Modifier
                    .height(6.dp)          // 轨道粗细
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
            ) {

                // ⚡ 背景轨道（右侧未播放）
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color(0xFF3A3A3A))  // 深灰色
                )

                // ⚡ 前景轨道（左侧播放进度 + 渐变）
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressFraction)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF527AFF),
                                    Color(0xFF7F5BFF)
                                )
                            )
                        )
                )
            }
        },
        thumb = {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color.White, CircleShape)         // 外层白色
                    .padding(4.dp)                                 // 白边厚度
                    .background(Color(0xFF527AFF), CircleShape)    // 内部蓝色
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentPlayListBottomSheet(currentList:MutableList<MusicInfo>, showSheet:Boolean, switchMode:(Int)->Unit, onDismiss: (MusicInfo?) -> Unit) {
    if (showSheet) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        // 设置全宽全屏
        ModalBottomSheet(
            onDismissRequest = { onDismiss(null) },
            sheetState = sheetState,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(), // 保证底部不挡导航栏
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            scrimColor = Color(0x80000000) // 遮罩透明度可调
        ) {
            CurrentPlayListItem(
                currentList = currentList,
                switchMode = switchMode,
                onClose = { onDismiss(it) }
            )
        }
    }
}


@Composable
fun  CurrentPlayListItem(currentList:MutableList<MusicInfo>,switchMode:(Int)->Unit,onClose: (MusicInfo?) -> Unit){
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
                        .clickable { onClose(null) }
                )
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = context1.getString( R.string.current_play_list),
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

                Row(modifier = Modifier.fillMaxWidth()) {
                    ModeButtonBox(
                        modifier = Modifier
                            .height(28.dp)
                            .weight(1f)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF7165FF),
                                        Color(0xFF0059FF)
                                    )
                                ),
                                shape = RoundedCornerShape(88.dp)
                            ).padding(horizontal = 8.dp, vertical = 4.dp)
                    ){
                        switchMode.invoke(it)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(id = R.drawable.img_delete_play_list),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp).clickable {
                            MusicPlayerHelper.clearPlayList()
                            onClose(null)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                ) {
                    items(currentList.size) { index ->
                        CurrentPlayListItemItem(
                            index,
                            musicInfo = currentList[index],
                            currentList
                        ) {
                            onClose.invoke(it)
                        }
                    }
                }

            }
        }
    }

}

@Composable
fun CurrentPlayListItemItem(index:Int,musicInfo: MusicInfo, currentList: MutableList<MusicInfo>, onClose: (MusicInfo?) -> Unit) {

    var isPlaying by remember { mutableStateOf(MusicPlayerHelper.isPlayingMusic(musicInfo)) }

    // 1. 加载动画资源
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.play_music_loading))

    // 2. 控制动画播放状态
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever, // 无限循环
        isPlaying = true,
        speed = 1.0f
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .padding(vertical = 8.dp).clickable{
                MusicPlayerHelper.playMusic(musicInfo, true)
                onClose(null)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (isPlaying) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .height(18.dp)
                    .width(17.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))
        }

        Text(
            text = musicInfo.displayName,
            color = if (isPlaying)  Color0059FF else Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.width(12.dp))


        Text(
            text = musicInfo.singerName,
            color = if (isPlaying) Color0059FF else Color6A6F87,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.weight(1f))


        Image(
            painter = painterResource(id = R.drawable.img_delete_play_item),
            contentDescription = null,
            modifier = Modifier.size(20.dp).clickable{
                onClose(musicInfo)
            }
        )

    }
}



