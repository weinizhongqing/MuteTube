package com.tb.music.player.ui.screen.nointernet

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tb.music.player.BuildConfig
import com.tb.music.player.R
import com.tb.music.player.ui.router.Router
import com.tb.music.player.ui.screen.TopBar
import com.tb.music.player.utils.LocalizedContext
import com.tb.music.player.utils.openContent


@Composable
fun NoInternetSettings() {

    val context = LocalContext.current
    val activity = context as? ComponentActivity

    BackHandler {
        activity?.finish()
    }

    LocalizedContext { context1 ->

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        TopBar(R.drawable.img_settings_name,false)
        Spacer(Modifier.height(48.dp))

        Column(modifier = Modifier.padding(horizontal = 36.dp)
            .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Row(modifier = Modifier.fillMaxWidth().height(40.dp).clickable {
                Router.navigate(Router.Route.LANGUAGE)
            }) {
                Image(
                    painter = painterResource(id = R.drawable.img_language_tip),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = context1.getString(R.string.language),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Image(
                    painter = painterResource(id = R.drawable.img_end_tip),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth().height(40.dp).clickable{
                openContent(context1,"https://sites.google.com/view/mutetube/home")
            }) {
                Image(
                    painter = painterResource(id = R.drawable.img_privacy_policy_tip),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = context1.getString(R.string.privacy_policy),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Image(
                    painter = painterResource(id = R.drawable.img_end_tip),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }


            Spacer(Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth().height(40.dp).clickable{
                openContent(context1,"https://play.google.com/store/apps/details?id=com.music.mute.feel.soulsound.tube")
            }) {
                Image(
                    painter = painterResource(id = R.drawable.img_share_tip),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = context1.getString(R.string.share_app),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Image(
                    painter = painterResource(id = R.drawable.img_end_tip),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }


            Spacer(Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.img_about_tip),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = context1.getString(R.string.version),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "V${BuildConfig.VERSION_NAME}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }

        }


    }
}

}
