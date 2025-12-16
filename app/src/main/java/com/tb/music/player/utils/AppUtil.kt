package com.tb.music.player.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun shareSong(context: Context, songUri: Uri) {
    // 创建分享 Intent
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        putExtra(Intent.EXTRA_STREAM, songUri) // 传递歌曲的 URI
        //putExtra(Intent.EXTRA_TEXT, songTitle) // 可选，分享的文本
        type = "audio/*" // 设置 MIME 类型
    }

    // 启动分享对话框
    context.startActivity(shareIntent)
}


fun Long.getDate(): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.format(Date(this))
}

fun formatDuration(time: Long): String {
    val format = SimpleDateFormat("mm:ss", Locale.getDefault())
    return format.format(Date(time))
}

/**
 * 将 dp 转换为 px 的扩展函数
 */
fun Context.dip2px(dp: Int): Int =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).toInt()

fun openContent(context: Context, content: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(content)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {

    }
}