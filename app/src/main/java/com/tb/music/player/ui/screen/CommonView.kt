package com.tb.music.player.ui.screen

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tb.music.player.R
import com.tb.music.player.TB
import com.tb.music.player.config.AppConfig
import com.tb.music.player.music.MusicPlayerHelper
import com.tb.music.player.music.status.PlayState
import com.tb.music.player.music.status.PlayStatus
import com.tb.music.player.ui.router.Router
import com.tb.music.player.ui.theme.Color111043
import com.tb.music.player.ui.theme.Color6A6F87

@Composable
fun TopBar(imgRedId: Int, type: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imgRedId),
            contentDescription = null,
            modifier = Modifier
                .width(117.dp)
                .height(37.dp)
        )

        Spacer(modifier = Modifier.width(29.dp))
        if (type) {
            Row(
                modifier = (Modifier
                    .weight(1f)
                    .height(32.dp)
                    .background(Color111043, RoundedCornerShape(24.dp))
                    .padding(horizontal = 12.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_search_tips),
                    contentDescription = null,
                    modifier = Modifier
                        .size(15.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = stringResource(id = R.string.search),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color6A6F87,
                )
            }
        }
    }
}


@Composable
fun PlayMusicView(){

    val musicInfo by MusicPlayerHelper.playMusic.observeAsState()
    val playState by MusicPlayerHelper.playState.observeAsState()
    val playProgress by MusicPlayerHelper.playProgress.observeAsState()
    val duration = playProgress?.first?.toFloat() ?: 0f
    val position = playProgress?.second?.toFloat() ?: 0f
    Column(
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = Color6A6F87
                )
                .padding(horizontal = 16.dp).clickable{
                    Router.navigate(Router.Route.playMusic(true))
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.img_bottom_default),
                contentDescription = null,
                modifier = Modifier.size(42.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)){
                Text(
                    text = musicInfo?.displayName?:"",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = musicInfo?.singerName?:"",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            }


            Image(
                painter = painterResource(id = R.drawable.img_bottom_music_last),
                contentDescription = null,
                modifier = Modifier.size(32.dp).clickable{
                    TB.instance.sendBroadcast(Intent(MusicPlayerHelper.Action.ACTION_PREVIOUS).apply {
                        setPackage(TB.instance.packageName)
                    })
                }
            )

            Spacer(Modifier.width(17.dp))

            Image(
                painter = painterResource(id = if (playState == PlayStatus.PLAYING) R.drawable.img_bottom_music_start else R.drawable.img_bottom_music_stop),
                contentDescription = null,
                modifier = Modifier.size(32.dp).clickable{
                    TB.instance.sendBroadcast(Intent(MusicPlayerHelper.Action.ACTION_PLAY_PAUSE).apply {
                        setPackage(TB.instance.packageName)
                    })
                }
            )

            Spacer(Modifier.width(17.dp))

            Image(
                painter = painterResource(id = R.drawable.img_bottom_music_next),
                contentDescription = null,
                modifier = Modifier.size(32.dp).clickable{
                    TB.instance.sendBroadcast(Intent(MusicPlayerHelper.Action.ACTION_NEXT_SONG).apply {
                        setPackage(TB.instance.packageName)
                    })
                }
            )
        }

        Log.d("CommonView", "duration: $duration, position: $position")

        GradientProgressBarCompose(position.toDouble(), duration.toDouble())
    }
}


@Composable
fun GradientProgressBarCompose(
    position: Double,
    duration: Double,
    height: Dp = 4.dp,
    startColor: Color = Color(0xFF0059FF),
    endColor: Color = Color(0xFF7165FF),
    backgroundColor: Color = Color(0x00000000)
) {
    val percent = ((position / duration).coerceIn(0.0, 1.0)).toFloat()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(percent)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(startColor, endColor)
                    )
                )
        )
    }
}



@Composable
fun ModeButtonBox( modifier:Modifier,switchMode:(Int)->Unit){

    var playMode by remember { mutableIntStateOf(AppConfig.playMode) }

    val iconRes = when (playMode) {
        0 -> R.drawable.img_order_play
        1 -> R.drawable.img_random_play
        2 -> R.drawable.img_cycle_play
        else -> 0
    }

    val textRes = when (playMode) {
        0 -> R.string.play_sequentially
        1 -> R.string.play_shuffle
        2 -> R.string.play_loop
        else -> 0
    }


    Box(
        modifier = modifier
            .clickable {
                // 点击事件
                val v = AppConfig.playMode + 1
                val newV = v % 3
                AppConfig.playMode = newV
                playMode = newV
                switchMode.invoke(newV)
            }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id =iconRes ),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.width(8.dp))


            Text(
                text = stringResource(id = textRes),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
            )
        }
    }
}


@Composable
fun ButtonBox(text: String,
              heightBox: Dp = 40.dp,
              horizontalPadding: Dp = 66.dp,
              roundedCornerShape: RoundedCornerShape = RoundedCornerShape(20.dp),
              onClick: () -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightBox)
            .padding(horizontal = horizontalPadding) // 注意：padding 应该在 fillMaxWidth 之后
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
            text =  text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center) // 改为 align
        )
    }
}


@Composable
fun GradientOutlineButton(
    text: String,
    heightBox: Dp = 40.dp,
    horizontalPadding: Dp = 66.dp,
    roundedCornerShape: RoundedCornerShape = RoundedCornerShape(20.dp),
    onClick: () -> Unit = {}
) {
    val gradientColors = listOf(
        Color(0xFF7165FF),
        Color(0xFF0059FF)
    )

    Box(
        modifier = Modifier
            .padding(horizontal = horizontalPadding)   // ✅ 放最外层
            .height(heightBox)
            .fillMaxWidth()
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(gradientColors),
                shape = roundedCornerShape
            )
            .clip(roundedCornerShape)  // clip 要在 border 后
            .clickable { onClick() }   // clickable 放最后
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}