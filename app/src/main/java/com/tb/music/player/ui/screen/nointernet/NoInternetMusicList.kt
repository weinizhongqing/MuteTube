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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tb.music.player.R
import com.tb.music.player.music.db.MusicDataHelper
import com.tb.music.player.music.db.info.MusicInfo
import com.tb.music.player.music.db.info.PlayListInfo
import com.tb.music.player.ui.router.Router
import com.tb.music.player.ui.screen.ButtonBox
import com.tb.music.player.ui.screen.PlayMusicView
import com.tb.music.player.ui.screen.TopBar
import com.tb.music.player.ui.screen.internet.SectionHeader
import com.tb.music.player.ui.theme.Color393C4C
import com.tb.music.player.ui.theme.Color6A6F87
import com.tb.music.player.utils.LocalizedContext

@Composable
fun NoInternetMusicList() {

    val context = LocalContext.current
    val activity = context as? ComponentActivity

    BackHandler {
        activity?.finish()
    }



    val musicList = remember { mutableStateListOf<PlayListInfo>() }

    var isCreateBottomSheet by remember { mutableStateOf(false) }


    fun refreshMusicList() {
        MusicDataHelper.getAllPlayList(false) { list ->
            musicList.clear()
            musicList.addAll(list)
        }
    }


    val recentlyMusicList = remember { mutableStateListOf<MusicInfo>() }

    // 加载数据
    LaunchedEffect(Unit) {
        MusicDataHelper.getPlayList(PlayListInfo.PlayListType.TYPE_RECENTLY) { list ->
            val playList = list.find { it.playerListType == PlayListInfo.PlayListType.TYPE_RECENTLY }
            playList?.let {
                MusicDataHelper.getPlayListMusic(it.playerListId) {
                    recentlyMusicList.addAll(it)
                }
            }
        }
    }


// 初次加载
    LaunchedEffect(Unit) {
        refreshMusicList()
    }

    LocalizedContext { context1 ->

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            TopBar(R.drawable.img_my_music_name, false)
            Spacer(Modifier.height(21.dp))

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(64.dp)
                    .clickable {
                        isCreateBottomSheet = true
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

            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {
                items(musicList.size) { index ->
                    NoMusicItem(musicList[index])
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(context1.getString(R.string.recently_played)) {
                MusicDataHelper.getPlayList(PlayListInfo.PlayListType.TYPE_RECENTLY) { list ->
                    val playList =
                        list.find { it.playerListType == PlayListInfo.PlayListType.TYPE_RECENTLY }
                    playList?.let {
                        Router.Route.navigatePlayListDesc(it)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(recentlyMusicList.size) { index ->
                    SinglePlayList(index, recentlyMusicList[index], recentlyMusicList)
                }
            }


            PlayMusicView()

        }

        if (isCreateBottomSheet) {
            CreateMusicBottomSheet(
                onClose = {
                    if (it) {
                        refreshMusicList()
                    }
                    isCreateBottomSheet = false
                }
            )
        }
    }
}



@Composable
fun NoMusicItem(
    playListInfo: PlayListInfo
){
    LocalizedContext { context1 ->
        when (playListInfo.playerListType) {
            PlayListInfo.PlayListType.TYPE_COLLECT -> {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp).clickable {
                            Router.Route.navigatePlayListDesc(playListInfo)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_my_music_like_tip),
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
                            text = context1.getString( R.string.num_songs, playListInfo.musicCount.toString()),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp).clickable {
                            Router.Route.navigatePlayListDesc(playListInfo)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_my_music_custom_tip),
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
                            text = context1.getString( R.string.num_songs, playListInfo.musicCount.toString()),
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
fun CreateMusicBottomSheet(onClose: (Boolean) -> Unit){
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // 设置全宽全屏
    ModalBottomSheet(
        onDismissRequest = { onClose(false) },
        sheetState = sheetState,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(), // 保证底部不挡导航栏
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,
        scrimColor = Color(0x80000000) // 遮罩透明度可调
    ) {

        CreateMusicPlayListBottomSheetContent(
            onClose = onClose
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMusicPlayListBottomSheetContent(
    onClose: (Boolean) -> Unit
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
                    text =context1.getString( R.string.create_new_play_list),
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
                            context1.getString(R.string.enter_new_play_list_name),
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
                            onClose.invoke(true)
                        }
                    }
                }

            }
        }
    }

}
