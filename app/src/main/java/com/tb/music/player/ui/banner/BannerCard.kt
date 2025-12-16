package com.tb.music.player.ui.banner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.tb.music.player.ui.model.BannerModel

@Composable
fun BannerCard(
    bean: BannerModel,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
    contentScale: ContentScale,
    imageRatio: Float = 2f,
    onBannerClick: () -> Unit,
) {
    if (bean.data.isEmpty()) {
        throw NullPointerException("Url or imgRes or filePath must have a not for empty.")
    }

    Card(
        shape = shape,
        modifier = modifier
    ) {
        val imgModifier = Modifier.clickable(onClick = onBannerClick)
        ImageLoader(bean.data, imgModifier.aspectRatio(imageRatio), contentScale)
    }
}