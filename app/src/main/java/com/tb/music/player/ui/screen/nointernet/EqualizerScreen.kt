package com.tb.music.player.ui.screen.nointernet

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.isVisible
import com.tb.music.player.R
import com.tb.music.player.config.AppConfig
import com.tb.music.player.music.equalizer.EqualizerHelper
import com.tb.music.player.pus.AdLoadingDialog
import com.tb.music.player.pus.BackAdHandler
import com.tb.music.player.pus.PusPos
import com.tb.music.player.pus.rememberAdController
import com.tb.music.player.ui.router.Router
import com.tb.music.player.ui.screen.GradientOutlineButton
import com.tb.music.player.ui.screen.internet.gradientBorder
import com.tb.music.player.utils.LocalizedContext

data class EqualizerInfo(
    val imgRedId: Int,
    val className: String,
    val type: Int
)


@Composable
fun EqualizerScreen() {

    LocalizedContext { context1 ->

        val equalizerInfoList = remember { mutableStateListOf<EqualizerInfo>() }

        // 加载数据
        LaunchedEffect(Unit) {
            equalizerInfoList.addAll(
                listOf(
                    EqualizerInfo(R.drawable.img_equalizer_custom, context1.getString(R.string.custom), 100),
                    EqualizerInfo(R.drawable.img_equalizer_classical, context1.getString(R.string.classical), 1),
                    EqualizerInfo(R.drawable.img_equalizer_flat, context1.getString(R.string.flat), 3),
                    EqualizerInfo(R.drawable.img_equalizer_folk, context1.getString(R.string.folk), 4),
                    EqualizerInfo(R.drawable.img_equalizer_dance, context1.getString(R.string.dance),  2),
                    EqualizerInfo(R.drawable.img_equalizer_heavy_metal, context1.getString(R.string.heavy_metal), 5),
                    EqualizerInfo(R.drawable.img_equalizer_hip_hop, context1.getString(R.string.hip_hop), 6),
                    EqualizerInfo(R.drawable.img_equalizer_jazz, context1.getString(R.string.jazz), 7),
                    EqualizerInfo(R.drawable.img_equalizer_pop, context1.getString(R.string.pop), 8),
                    EqualizerInfo(R.drawable.img_equalizer_rock, context1.getString(R.string.rock), 9),
                )
            )
        }

        var selectType by remember { mutableIntStateOf(AppConfig.selectPresetIndex) }


        var isCustom by remember { mutableStateOf(false) }

        var verticalSeekBar1 by remember {
            mutableIntStateOf(
                AppConfig.getEqualizerHz(0).takeIf { it != -1 } ?: 60)
        }
        var verticalSeekBar2 by remember {
            mutableIntStateOf(
                AppConfig.getEqualizerHz(1).takeIf { it != -1 } ?: 230)
        }
        var verticalSeekBar3 by remember {
            mutableIntStateOf(
                AppConfig.getEqualizerHz(2).takeIf { it != -1 } ?: 910)
        }
        var verticalSeekBar4 by remember {
            mutableIntStateOf(
                AppConfig.getEqualizerHz(3).takeIf { it != -1 } ?: 4000)
        }
        var verticalSeekBar5 by remember {
            mutableIntStateOf(
                AppConfig.getEqualizerHz(4).takeIf { it != -1 } ?: 14000)
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
                .navigationBarsPadding()
        ) {
            // 背景图片
            Image(
                painter = painterResource(id = R.drawable.img_equalizer_bg), // 替换为你的图片资源
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                contentScale = ContentScale.Crop // 填充整个Box
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_back_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                // 处理点击事件
                                handleBack()
                            },
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    if (isCustom) {
                        TitleButtonBox(text = R.string.apply) {

                            AppConfig.setEqualizerHz(0, verticalSeekBar1)
                            EqualizerHelper.instance.setEqualizer(0, verticalSeekBar1)

                            AppConfig.setEqualizerHz(1, verticalSeekBar2)
                            EqualizerHelper.instance.setEqualizer(1, verticalSeekBar2)

                            AppConfig.setEqualizerHz(2, verticalSeekBar3)
                            EqualizerHelper.instance.setEqualizer(2, verticalSeekBar3)

                            AppConfig.setEqualizerHz(3, verticalSeekBar4)
                            EqualizerHelper.instance.setEqualizer(3, verticalSeekBar4)

                            AppConfig.setEqualizerHz(4, verticalSeekBar5)
                            EqualizerHelper.instance.setEqualizer(4, verticalSeekBar5)
                            AppConfig.selectPresetIndex = 100
                            selectType = 100
                            isCustom = false
                        }
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                if (!isCustom) {

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(equalizerInfoList) { item ->
                            EqualizerItem(
                                equalizerInfo = item,
                                isSelected = item.type == selectType,
                                callback = {
                                    if (it != 100) {
                                        selectType = it
                                        AppConfig.selectPresetIndex = it
                                    } else {
                                        isCustom = true
                                    }
                                }
                            )
                        }
                    }
                } else {

                    GradientOutlineButton(context1.getString(R.string.custom) ) {}

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
                            .height(344.dp).gradientBorder(
                            colors = listOf(
                                Color(0x2BFFFFFF),
                                Color(0x00FFFFFF)
                            ),
                            cornerRadius = 24.dp,
                            strokeWidth = 1.dp
                        ).background(
                            color = Color(0x21FFFFFF),
                            shape = RoundedCornerShape(24.dp)
                        )
                    ) {

                        // 保存进度的状态

                        Spacer(modifier = Modifier.weight(1f))

                        Column {
                            VerticalSeekBar(
                                modifier = Modifier
                                    .padding(top = 24.dp)
                                    .weight(1f)
                                    .width(24.dp),    // 控制宽度
                                progress = verticalSeekBar1,
                                onProgressChange = { newProgress ->
                                    verticalSeekBar1 = newProgress
                                },
                                min = 30,
                                max = 120,
                            )
                            Text(
                                text = "250",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Normal
                            )

                            Spacer(modifier = Modifier.weight(0.2f))
                        }


                        Spacer(modifier = Modifier.weight(1f))



                        Column {
                            VerticalSeekBar(
                                modifier = Modifier
                                    .padding(top = 24.dp)
                                    .weight(1f)
                                    .width(24.dp),    // 控制宽度
                                progress = verticalSeekBar2,
                                onProgressChange = { newProgress ->
                                    verticalSeekBar2 = newProgress
                                },
                                min = 120,
                                max = 460,
                            )
                            Text(
                                text = "500",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Normal
                            )

                            Spacer(modifier = Modifier.weight(0.2f))
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Column {
                            VerticalSeekBar(
                                modifier = Modifier
                                    .padding(top = 24.dp)
                                    .weight(1f)
                                    .width(24.dp),    // 控制宽度
                                progress = verticalSeekBar3,
                                onProgressChange = { newProgress ->
                                    verticalSeekBar3 = newProgress
                                },
                                min = 460,
                                max = 1800,
                            )
                            Text(
                                text = "1k",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Normal
                            )

                            Spacer(modifier = Modifier.weight(0.2f))
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Column {
                            VerticalSeekBar(
                                modifier = Modifier
                                    .padding(top = 24.dp)
                                    .weight(1f)
                                    .width(24.dp),    // 控制宽度
                                progress = verticalSeekBar4,
                                onProgressChange = { newProgress ->
                                    verticalSeekBar4 = newProgress
                                },
                                min = 1800,
                                max = 7000,
                            )
                            Text(
                                text = "2k",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Normal
                            )

                            Spacer(modifier = Modifier.weight(0.2f))
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Column {
                            VerticalSeekBar(
                                modifier = Modifier
                                    .padding(top = 24.dp)
                                    .weight(1f)
                                    .width(24.dp),    // 控制宽度
                                progress = verticalSeekBar5,
                                onProgressChange = { newProgress ->
                                    verticalSeekBar5 = newProgress
                                },
                                min = 7000,
                                max = 20000,
                            )
                            Text(
                                text = "4k",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Normal
                            )

                            Spacer(modifier = Modifier.weight(0.2f))
                        }

                        Spacer(modifier = Modifier.weight(1f))

                    }


                }
            }

        }

    }

}



@Composable
fun VerticalSeekBar(
    modifier: Modifier = Modifier,
    progress: Int,
    onProgressChange: (Int) -> Unit,
    min: Int = 0,
    max: Int = 100,
    trackColor: Color = Color(0xFF6A6F87),
    progressColor: Color = Color(0xFF0059FF),
    thumbColor: Color = Color(0xFFFFFFFF),
    thumbRadius: Dp = 12.dp,
    trackWidth: Dp = 7.dp,
    numberTextSize: TextUnit = 12.sp,
    numberMargin: Dp = 15.dp,
    lineSpacing: Dp = 5.dp
) {
    // 内部状态
    var internalProgress by remember { mutableIntStateOf(progress.coerceIn(min, max)) }

    // 可拖动状态
    val dragModifier = Modifier.pointerInput(Unit) {
        detectVerticalDragGestures { change, _ ->
            val y = change.position.y
            val height = size.height.toFloat() - thumbRadius.toPx() * 2
            val trackTop = thumbRadius.toPx()
            val trackBottom = height
            val trackHeight = trackBottom - trackTop

            // 计算进度
            val rawProgress = ((1 - (y - trackTop) / trackHeight) * (max - min) + min).toInt()
            internalProgress = rawProgress.coerceIn(min, max)
            onProgressChange(internalProgress)
        }
    }

    BoxWithConstraints(
        modifier = modifier.then(dragModifier),
        contentAlignment = Alignment.TopCenter
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        val trackTop = thumbRadius.value
        val trackBottom = heightPx - (numberTextSize.value * 2 + lineSpacing.value + numberMargin.value)
        val trackHeight = trackBottom - trackTop

        val normalizedProgress = (internalProgress - min).toFloat() / (max - min)
        val progressHeight = trackHeight * (1 - normalizedProgress)
        val thumbCenterY = (trackTop + progressHeight).coerceIn(trackTop, trackBottom)

        // 绘制 Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = widthPx / 2f

            // 绘制轨道
            drawRoundRect(
                color = trackColor,
                topLeft = Offset(centerX - trackWidth.toPx() / 2, trackTop),
                size = Size(trackWidth.toPx(), trackHeight),
                cornerRadius = CornerRadius(trackWidth.toPx() / 2)
            )

            // 绘制进度
            drawRoundRect(
                color = progressColor,
                topLeft = Offset(centerX - trackWidth.toPx() / 2, thumbCenterY),
                size = Size(trackWidth.toPx(), trackBottom - thumbCenterY),
                cornerRadius = CornerRadius(trackWidth.toPx() / 2)
            )

            // 绘制滑块
            drawCircle(
                color = thumbColor,
                radius = thumbRadius.toPx(),
                center = Offset(centerX, thumbCenterY)
            )
        }
    }
}






@Composable
fun EqualizerItem(equalizerInfo: EqualizerInfo,isSelected: Boolean,callback: (Int) -> Unit){


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(207.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) Color(0xFF4C7DFF) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ).clickable{
                callback.invoke(equalizerInfo.type)
            }
    ) {
        Image(
            painter = painterResource(id = equalizerInfo.imgRedId),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Text(
            text = equalizerInfo.className,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp).align(Alignment.BottomCenter) // 改为 align
        )

        if (isSelected){
            Image(
                painter = painterResource(id = R.drawable.img_equalizer_select),
                contentDescription = null,
                modifier = Modifier
                    .padding(12.dp)
                    .size(16.dp).align(Alignment.TopEnd),
                contentScale = ContentScale.Crop
            )
        }

    }

}



@Composable
fun TitleButtonBox(text: Int,
              heightBox: Dp = 31.dp,
              widthBox: Dp = 80.dp,
              roundedCornerShape: RoundedCornerShape = RoundedCornerShape(88.dp),
              onClick: () -> Unit
){
    Box(
        modifier = Modifier
            .height(heightBox)
            .width(widthBox)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF7165FF),
                        Color(0xFF0059FF)
                    )
                ),
                shape = roundedCornerShape
            )
            .clickable {
                // 点击事件
                onClick.invoke()
            }
    ) {
        Text(
            text = stringResource(id = text),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center) // 改为 align
        )
    }
}