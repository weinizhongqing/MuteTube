package com.tb.music.player.ui.screen.nointernet

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironsource.fa
import com.tb.music.player.R
import com.tb.music.player.pus.AdLoadingDialog
import com.tb.music.player.pus.PusPos
import com.tb.music.player.pus.rememberAdController
import com.tb.music.player.pus.rememberPusView
import com.tb.music.player.ui.router.Router
import com.tb.music.player.ui.screen.PlayMusicView
import com.tb.music.player.ui.screen.TopBar
import com.tb.music.player.ui.theme.Color111043
import com.tb.music.player.ui.theme.Color6A6F87
import com.tb.music.player.utils.LocalizedContext

@Composable
fun NoInternetHome() {

    val context = LocalContext.current
    val activity = context as? ComponentActivity

    BackHandler {
        activity?.finish()
    }

    val adController = rememberAdController()

    var showLoading by remember { mutableStateOf(false) }

    AdLoadingDialog(show = showLoading)


    LocalizedContext{ context1 ->


    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_home_app_name),
                contentDescription = null,
                modifier = Modifier
                    .width(117.dp)
                    .height(37.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.img_title_scan),
                contentDescription = null,
                modifier = Modifier
                    .width(32.dp)
                    .height(32.dp).clickable{
                        Router.navigate(Router.Route.SCAN_LOCAL_MUSIC)
                    }
            )


        }

        Spacer(Modifier.height(21.dp))

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(182.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable {
                    // 处理点击事件
                    adController.showDelayInterAd(
                        position = PusPos.AD_CLICK_I,
                        onLoading = {showLoading = false}
                    ){
                        Router.navigate(Router.Route.NO_SINGLE)
                    }

                }) {
            Image(
                painter = painterResource(id = R.drawable.img_home_single),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // 填充整个Box
            )

            Text(
                text = context1.getString(R.string.single),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .align(Alignment.BottomCenter)
            )

        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(229.dp)
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(229.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        adController.showDelayInterAd(PusPos.AD_CLICK_I, onLoading = {showLoading = false}){
                            Router.navigate(Router.Route.NO_SINGER)
                        }
                    }) {

                Image(
                    painter = painterResource(id = R.drawable.img_home_singer),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // 填充整个Box
                )

                Text(
                    text = context1.getString(R.string.singer) ,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .align(Alignment.BottomCenter)
                )


            }

            Spacer(Modifier.width(12.dp))


            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(229.dp)
            ) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(229.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            adController.showDelayInterAd(PusPos.AD_CLICK_I, onLoading = {showLoading = false}){
                                Router.navigate(Router.Route.NO_ALBUM)
                            }
                        }) {

                    Image(
                        painter = painterResource(id = R.drawable.img_home_album),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop // 填充整个Box
                    )

                    Text(
                        text = context1.getString(R.string.album),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                            .align(Alignment.BottomCenter)
                    )


                }


                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(229.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            adController.showDelayInterAd(PusPos.AD_CLICK_I, onLoading = {showLoading = false}){
                                Router.navigate(Router.Route.EQUALIZER)
                            }
                        }) {

                    Image(
                        painter = painterResource(id = R.drawable.img_home_equalizer),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop // 填充整个Box
                    )

                    Text(
                        text = context1.getString(R.string.equalizer),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                            .align(Alignment.BottomCenter)
                    )

                }

            }

        }

        Spacer(Modifier.weight(1f))

        PlayMusicView()
    }
}

}


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