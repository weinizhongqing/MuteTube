package com.tb.music.player.ui.banner

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter


private const val TAG = "ImageLoader"
@Composable
fun ImageLoader(
    data: Any?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    when (data) {
        is String -> {
            val painter = if (data.contains("https://") || data.contains("http://")) {
                Log.d(TAG, "PostCardPopular: 加载网络图片")
                rememberAsyncImagePainter(
                    model = data,
                )
            } else {
                Log.d(TAG, "PostCardPopular: 加载本地图片")
                val bitmap = BitmapFactory.decodeFile(data)
                BitmapPainter(bitmap.asImageBitmap())
            }
            Image(
                modifier = modifier,
                painter = painter,
                contentDescription = "",
                contentScale = contentScale
            )
        }
        is Int -> {
            Log.d(TAG, "PostCardPopular: 加载本地资源图片")
            Image(
                modifier = modifier,
                painter = painterResource(data),
                contentDescription = "",
                contentScale = contentScale
            )
        }
        else -> {
            throw IllegalArgumentException("参数类型不符合要求，只能是：url、文件路径或者是 drawable id")
        }
    }
}