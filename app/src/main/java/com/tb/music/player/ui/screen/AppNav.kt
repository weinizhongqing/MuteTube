package com.tb.music.player.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tb.music.player.R
import com.tb.music.player.config.AppConfig
import com.tb.music.player.music.db.info.PlayListInfo
import com.tb.music.player.ui.router.Router
import com.tb.music.player.ui.screen.internet.MustSongsScreen
import com.tb.music.player.ui.screen.nointernet.AlbumScreen
import com.tb.music.player.ui.screen.nointernet.EqualizerScreen
import com.tb.music.player.ui.screen.nointernet.NoAlbumListScreen
import com.tb.music.player.ui.screen.nointernet.NoSingerListScreen
import com.tb.music.player.ui.screen.nointernet.PlayListDescScreen
import com.tb.music.player.ui.screen.nointernet.PlayMusicScreen
import com.tb.music.player.ui.screen.nointernet.ScanLocalMusicScreen
import com.tb.music.player.ui.screen.nointernet.SingerScreen
import com.tb.music.player.ui.screen.nointernet.SingleScreen
import kotlinx.coroutines.delay

@Composable
fun AppNav(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Router.Route.START
    ) {
        composable(Router.Route.START) { backStack ->
            val id = backStack.arguments?.getString("type") ?: "false"
            StartScreen(id.toBoolean())
        }
        composable(Router.Route.MAIN){
            AppRoot()
        }
        composable(Router.Route.MUST_SONGS){
            MustSongsScreen()
        }
        composable(Router.Route.LANGUAGE){
            LanguageScreen()
        }

        composable(Router.Route.SCAN_LOCAL_MUSIC){
            ScanLocalMusicScreen()
        }

        composable(Router.Route.PLAY_MUSIC){ backStack ->
            val id = backStack.arguments?.getString("type") ?: "false"
            Log.d("PlayMusicScreen", "id: $id")
            PlayMusicScreen(id.toBoolean())
        }

        composable(Router.Route.NO_SINGLE){
            SingleScreen()
        }

        composable(Router.Route.NO_SINGER){
            SingerScreen()
        }

        composable(Router.Route.NO_SINGER_LIST){ backStackEntry ->
            val info = backStackEntry.savedStateHandle.get<PlayListInfo>("info")
            NoSingerListScreen(info)
        }

        composable(Router.Route.NO_ALBUM){
            AlbumScreen()
        }

        composable(Router.Route.NO_ALBUM_LIST){ backStackEntry ->
            val info = backStackEntry.savedStateHandle.get<PlayListInfo>("info")
            NoAlbumListScreen(info)
        }

        composable(Router.Route.EQUALIZER){
            EqualizerScreen()
        }


        composable(Router.Route.PLAY_LIST_DESC){ backStackEntry ->
            val info = backStackEntry.savedStateHandle.get<PlayListInfo>("info")
            PlayListDescScreen(info)
        }
    }
}



@Composable
fun AppLocalTitle(
                  title: String,
                  isBackVisible: Boolean = false,
                  isOkVisible: Boolean = false,
                  onBackClick: (() -> Unit)? = null,
                  onOkClick: (() -> Unit)? = null){

    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth().height(40.dp),
                horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically ) {

        if(isBackVisible){
            Image(
                painter = painterResource(id = R.drawable.img_back_screen),
                contentDescription = null,
                modifier = Modifier.fillMaxHeight().padding(horizontal = 12.dp).clickable{
                    onBackClick?.invoke()
                }
            )
        }

        Text(
            text = title,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            fontSize = 18.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        if(isOkVisible){
            Text(
                text = stringResource(id = R.string.ok),
                modifier = Modifier.fillMaxHeight().padding(horizontal = 12.dp).clickable{
                    onOkClick?.invoke()
                },
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}