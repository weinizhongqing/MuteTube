package com.tb.music.player.ui.screen.nointernet

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ironsource.fa
import com.tb.music.player.R
import com.tb.music.player.TB
import com.tb.music.player.config.AppConfig
import com.tb.music.player.music.MusicPlayerHelper
import com.tb.music.player.music.db.MusicDataHelper
import com.tb.music.player.music.db.info.MusicInfo
import com.tb.music.player.music.scan.ScanMusicHelper
import com.tb.music.player.pus.AdLoadingDialog
import com.tb.music.player.pus.OnResumeEffect
import com.tb.music.player.pus.PusPos
import com.tb.music.player.pus.rememberAdController
import com.tb.music.player.pus.rememberPusView
import com.tb.music.player.ui.router.Router
import com.tb.music.player.ui.screen.AppLocalTitle
import com.tb.music.player.ui.screen.ButtonBox
import com.tb.music.player.ui.screen.GradientOutlineButton
import com.tb.music.player.ui.theme.Color6A6F87
import com.tb.music.player.utils.LocalizedContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ScanLocalMusicScreen() {

    var backVisible by remember { mutableStateOf(AppConfig.isScanLocalMusicFirst) }
    var isScanning by remember { mutableStateOf(0) }
    var requestPermission by remember { mutableStateOf(false) }


    // 1. 加载动画资源
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.scan_music_loading))

    // 2. 控制动画播放状态
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever, // 无限循环
        isPlaying = true,
        speed = 1.0f
    )

    val adController = rememberAdController()
    val pusView = rememberPusView()
    var hasRequested by remember { mutableStateOf(false) }

    var showLoading by remember { mutableStateOf(false) }

    AdLoadingDialog(show = showLoading)

    OnResumeEffect {
        if (!hasRequested) {
            hasRequested = true
            adController.showNativeAd(
                position = PusPos.AD_SCAN_N,
                view = pusView,
                isV = true
            )
        }
    }

    val musicList = remember { mutableStateListOf<MusicInfo>() }

    // 加载数据
    LaunchedEffect(Unit) {
        MusicDataHelper.getPlayListMusic(MusicDataHelper.singleListId) { list ->
            musicList.addAll(list)
        }
    }


    LocalizedContext { context1 ->

        Box(modifier = Modifier.windowInsetsPadding(WindowInsets(0, 0, 0, 0))) {
            // 背景图片
            Image(
                painter = painterResource(id = R.drawable.img_home_bg), // 替换为你的图片资源
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // 填充整个Box
            )

            Column(
                modifier = Modifier
                    .fillMaxSize().statusBarsPadding().navigationBarsPadding()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    AppLocalTitle(
                        title = if (isScanning == 2 || isScanning == 3) context1.getString(R.string.scan_results) else context1.getString(R.string.scan),
                        isBackVisible = backVisible,
                        onBackClick = {
                            Router.back()
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isScanning == 1) {
                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.size(210.dp).align(Alignment.CenterHorizontally)
                        )
                    } else if (isScanning == 0) {
                        Image(
                            painter = painterResource(id = R.drawable.img_scan_tip),
                            contentDescription = null,
                            modifier = Modifier
                                .size(210.dp)
                                .align(Alignment.CenterHorizontally),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (isScanning == 1) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = context1.getString(R.string.scanning),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = context1.getString(R.string.scanning_tip),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color6A6F87,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                .padding(horizontal = 16.dp)
                        )

                    } else if (isScanning == 0) {
                        Spacer(modifier = Modifier.height(60.dp))

                        ButtonBox(text = context1.getString(R.string.scan)) {
                            requestPermission = true
                        }
                    }


                    if (requestPermission) {
                        RequestAudioPermission(
                            trigger = requestPermission,
                            onGranted = {
                                // 权限成功
                                isScanning = 1
                                TB.scope.launch(Dispatchers.IO) {
                                    ScanMusicHelper.scanAddMusic(isFilter = true) {
                                        ScanMusicHelper.addMusic(ScanMusicHelper.findMusicList)
                                        withContext(Dispatchers.Main) {
                                            adController.showDelayInterAd(position = PusPos.AD_SCAN_I, onLoading = {showLoading = false}){
                                                isScanning = if (ScanMusicHelper.findMusicList.isEmpty()) 2 else 3
                                            }
                                        }
                                    }
                                }
                            },
                            onFinish = {
                                // 不管成功拒绝，都会回调这里
                                requestPermission = false
                            }
                        )
                    }



                    if (isScanning == 2) {

                        Spacer(modifier = Modifier.height(16.dp))

                        Image(
                            painter = painterResource(id = R.drawable.img_scan_data_null),
                            contentDescription = null,
                            modifier = Modifier
                                .size(118.dp)
                                .align(Alignment.CenterHorizontally),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = context1.getString(R.string.no_content_scanning),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                .padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        GradientOutlineButton(text =  context1.getString(R.string.home)) {
                            adController.showDelayInterAd(position = PusPos.AD_SCAN_CLICK_I, onLoading = {showLoading = false}){
                                AppConfig.isScanLocalMusicFirst = true
                                Router.navigate(
                                    Router.Route.MAIN,
                                    popUpToRoute = Router.Route.SCAN_LOCAL_MUSIC,
                                    inclusive = true
                                )
                            }

                        }

                    } else if (isScanning == 3) {

                        Image(
                            painter = painterResource(id = R.drawable.img_scan_data_no_nullwebp),
                            contentDescription = null,
                            modifier = Modifier
                                .size(210.dp)
                                .align(Alignment.CenterHorizontally),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        GradientStringResource(
                            text = ScanMusicHelper.findMusicList.size.toString(),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                .padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        GradientOutlineButton(text = context1.getString(R.string.home)) {

                            adController.showDelayInterAd(position = PusPos.AD_SCAN_CLICK_I, onLoading = {showLoading = false}){
                                AppConfig.isScanLocalMusicFirst = true
                                Router.navigate(
                                    Router.Route.MAIN,
                                    popUpToRoute = Router.Route.SCAN_LOCAL_MUSIC,
                                    inclusive = true
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        ButtonBox(text = context1.getString(R.string.play_now)) {

                            adController.showDelayInterAd(position = PusPos.AD_SCAN_CLICK_I, onLoading = {showLoading = false}){
                                AppConfig.isScanLocalMusicFirst = true
                                val localPlayListId = MusicDataHelper.localListId
                                if (AppConfig.playListId == localPlayListId) {
                                    if (!MusicPlayerHelper.isPlaying) {
                                        TB.instance.sendBroadcast(Intent(MusicPlayerHelper.Action.ACTION_PLAY_PAUSE).apply {
                                            `package` = TB.instance.packageName
                                        })
                                    }
                                } else {
                                    MusicDataHelper.getPlayListMusic(localPlayListId) {
                                        MusicPlayerHelper.setPlayList(localPlayListId, 0, it, isPlay = true, isInit = false)
                                    }
                                }
                                Router.navigate(Router.Route.playMusic(false),popUpToRoute = Router.Route.SCAN_LOCAL_MUSIC,
                                    inclusive = true)
                            }
                        }

                    }
                }

                AndroidView(
                    modifier = Modifier.fillMaxWidth(),
                    factory = { pusView }
                )
            }
        }
    }
}





@Composable
fun RequestAudioPermission(
    trigger: Boolean,
    onGranted: () -> Unit,
    onFinish: () -> Unit   // 请求结束（成功或失败都通知）
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) onGranted()
        onFinish()
    }

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        Manifest.permission.READ_MEDIA_AUDIO
    else
        Manifest.permission.READ_EXTERNAL_STORAGE

    LaunchedEffect(trigger) {
        if (trigger) {
            if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                onGranted()
                onFinish()
            } else {
                permissionLauncher.launch(permission)
            }
        }
    }
}


@Composable
fun GradientStringResource(text: String,modifier: Modifier = Modifier) {
    LocalizedContext { context1 ->
        val raw = context1.getString(R.string.scan_successfully, text)
        val target = text

        val start = raw.indexOf(target)
        val end = start + target.length

        val gradientColors = listOf(
            Color(0xFFFF32C7),
            Color(0xFF5642DD),
        )

        val annotated = buildAnnotatedString {
            append(raw)
            addStyle(
                style = SpanStyle(
                    brush = Brush.linearGradient(gradientColors)
                ),
                start = start,
                end = end
            )
        }

        Text(text = annotated, fontSize = 16.sp, color = Color.White, modifier = modifier)
    }
}







