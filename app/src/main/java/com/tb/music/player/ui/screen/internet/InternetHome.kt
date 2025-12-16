package com.tb.music.player.ui.screen.internet

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tb.music.player.R
import com.tb.music.player.ui.banner.BannerPager
import com.tb.music.player.ui.model.BannerModel
import com.tb.music.player.ui.screen.TopBar
import com.tb.music.player.utils.LocalizedContext

@Composable
fun InternetHome() {
    LocalizedContext { context1 ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            item { TopBar(R.drawable.img_home_app_name, true) }
            item { Banner() }
            item { MustHeader(context1.getString(R.string.must_listen_songs)) }
            item { SectionHeader(context1.getString(R.string.popular_single)) {} }
            item { Spacer(Modifier.height(16.dp)) }
            item { PopularSingleList() }
            item { Spacer(Modifier.height(16.dp)) }
            item { SectionHeader(context1.getString(R.string.recommended_playlist)) {} }
            item { Spacer(Modifier.height(16.dp)) }
            item { PlaylistRow() }
            item { Spacer(Modifier.height(16.dp)) }
            item { SectionHeader(context1.getString(R.string.music_videos)) {} }
            item { Spacer(Modifier.height(16.dp)) }
            item { MusicVideoRow() }  // only 3 items
            item { Spacer(Modifier.height(16.dp)) }
            item { SectionHeader(context1.getString(R.string.artists)) {} }
            item { Spacer(Modifier.height(16.dp)) }
            item { ArtistsRow() }     // circular items
            item { Spacer(Modifier.height(16.dp)) }
            item { SectionHeader(context1.getString(R.string.new_album)) {} }
            item { Spacer(Modifier.height(16.dp)) }
            item { NewAlbumRow() }    // only 4 items
            item { Spacer(Modifier.height(10.dp)) }
        }

    }
}


@Composable
fun Banner() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .height(193.dp)
    ) {

        val items = arrayListOf(
            BannerModel(
                "https://wanandroid.com/blogimgs/8a0131ac-05b7-4b6c-a8d0-f438678834ba.png",
            ),
            BannerModel(
                "https://www.wanandroid.com/blogimgs/62c1bd68-b5f3-4a3c-a649-7ca8c7dfabe6.png",
            ),
            BannerModel(
                "https://www.wanandroid.com/blogimgs/50c115c2-cf6c-4802-aa7b-a4334de444cd.png",
            ),
            BannerModel(
                "https://www.wanandroid.com/blogimgs/90c6cc12-742e-4c9f-b318-b912f163b8d0.png",
            ),
        )

        BannerPager(
            items = items,
            indicatorGravity = Alignment.BottomStart
        ) { item ->
            Toast.makeText(context, "item:$item", Toast.LENGTH_SHORT).show()
        }


    }

}


@Composable
fun MustHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 26.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = R.drawable.img_home_music_tip),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text =  title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.img_home_music_right),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )

    }

}


@Composable
fun SectionHeader(title: String,click: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { click() },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_home_music_tip),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text =  title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }

        Box(
            modifier = Modifier
                .gradientBorder(
                    colors = listOf(
                        Color(0xFF83A6FF),
                        Color(0xFF0059FF)
                    ),
                    cornerRadius = 50.dp,
                    strokeWidth = 1.dp
                )
                .size(height = 18.dp, width = 59.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.view_all),
                fontSize = 12.sp,
                color = Color.White
            )
        }

    }
}


@Composable
fun PopularSingleList() {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(8) {
            SongItem()
        }
    }
}

@Composable
fun SongItem() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(80.dp)
            .width(295.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(color = Color(0xFF20222B), shape = RoundedCornerShape(20.dp)),
    ) {

        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 12.dp, end = 8.dp)
                .size(56.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(text = "As It Was", fontSize = 16.sp, color = Color.White)
            Text(text = "Harry Styles", fontSize = 14.sp, color = Color(0xff6A6F87))
        }

        Image(
            painter = painterResource(id = R.drawable.img_home_tip_menu),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 12.dp)
                .size(24.dp)
        )

    }
}


@Composable
fun PlaylistRow() {
    LazyRow(contentPadding = PaddingValues(horizontal = 20.dp)) {
        items(6) {
            PlaylistItem()
        }
    }
}

@Composable
fun PlaylistItem() {
    Column(
        modifier = Modifier
            .padding(end = 14.dp)
            .width(140.dp)
    ) {
        Box(
            modifier = Modifier
                .height(160.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )

            Image(
                painter = painterResource(id = R.drawable.img_home_tip_play),
                contentDescription = null,
                modifier = Modifier
                    .size(26.dp)
                    .align(Alignment.BottomEnd)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text("Bad Guy", color = Color.White)
        Text("Billie Eilish", color = Color.LightGray, fontSize = 12.sp)
    }
}

@Composable
fun MusicVideoRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(232.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color.Transparent, shape = RoundedCornerShape(20.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color(0x66000000), shape = RoundedCornerShape(32.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    text = "6:53",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            Image(
                painter = painterResource(id = R.drawable.img_home_tip_stop),
                contentDescription = null,
                modifier = Modifier
                    .padding(12.dp)
                    .size(26.dp)
                    .align(Alignment.TopEnd)
            )
        }

        Spacer(Modifier.width(12.dp))


        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Transparent, shape = RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color(0x66000000), shape = RoundedCornerShape(32.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .align(Alignment.BottomStart)
                ) {
                    Text(
                        text = "6:53",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.img_home_tip_stop),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(26.dp)
                        .align(Alignment.TopEnd)
                )
            }

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Transparent, shape = RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color(0x66000000), shape = RoundedCornerShape(32.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .align(Alignment.BottomStart)
                ) {
                    Text(
                        text = "6:53",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.img_home_tip_stop),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 12.dp, end = 12.dp)
                        .size(26.dp)
                        .align(Alignment.TopEnd)
                )
            }
        }
    }
}

@Composable
fun ArtistsRow() {
    LazyRow(contentPadding = PaddingValues(horizontal = 20.dp)) {
        items(6) {
            ArtistItem()
        }
    }
}

@Composable
fun ArtistItem() {
    Column(
        modifier = Modifier
            .padding(end = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = "https://picsum.photos/200",
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(6.dp))
        Text("Taylor Swift", color = Color.White, fontSize = 12.sp)
    }
}

@Composable
fun NewAlbumRow() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(4) {
            AlbumItem()
        }
    }
}

@Composable
fun AlbumItem() {
    Column(
        modifier = Modifier
            .padding(end = 14.dp)
            .width(140.dp)
    ) {
        Box(
            modifier = Modifier
                .height(160.dp)
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth()
                .background(Color(0xFF5653A6))
        )
        Spacer(Modifier.height(8.dp))
        Text("As It Was", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Normal)
        Text(
            "Harry Styles",
            color = Color(0xFF6A6F87),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    }
}


fun Modifier.gradientBorder(
    colors: List<Color>,
    cornerRadius: Dp = 8.dp,
    strokeWidth: Dp = 1.dp
): Modifier = this.then(
    Modifier.drawWithCache {
        val strokePx = strokeWidth.toPx()
        val radiusPx = cornerRadius.toPx()
        val gradientBrush = Brush.linearGradient(colors)

        onDrawWithContent {
            drawContent()

            drawRoundRect(
                brush = gradientBrush,
                cornerRadius = CornerRadius(radiusPx, radiusPx),
                style = Stroke(strokePx)
            )
        }
    }
)